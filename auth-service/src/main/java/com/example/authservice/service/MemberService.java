package com.example.authservice.service;


import com.example.authservice.DTO.LoginDTO;
import com.example.authservice.DTO.LoginResponse;
import com.example.authservice.DTO.ParticipantDTO;
import com.example.authservice.DTO.SignUpDTO;
import com.example.authservice.config.RedisPublisher;
import com.example.authservice.entity.Member;
import com.example.authservice.enums.*;
import com.example.authservice.exceptions.InvalidPhoneNumber;
import com.example.authservice.exceptions.UserNotFoundException;
import com.example.authservice.mail.EmailTemplateService;
import com.example.authservice.repository.MemberRepository;
import com.example.authservice.security.CustomUserDetails;
import com.example.authservice.security.jwt.JwtUtil;
import com.example.authservice.util.PhoneNumberValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class MemberService {

    private final MemberRepository memberRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final EmailTemplateService emailTemplateService;
    private final RedisPublisher redisPublisher;


    @Transactional(readOnly = true)
    public LoginResponse authenticateUser(LoginDTO dto) {
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(dto.email(), dto.password());
        Authentication authentication;

        try {
            authentication = authenticationManager.authenticate(token);
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            String jwtToken = jwtUtil.generateToken(userDetails);

            log.info("User {} successfully logged in", dto.email());

            log.info("creating chat participant : {}", dto.email());
            return new LoginResponse(jwtToken);
        } catch (BadCredentialsException e) {
            log.warn("user : {}, entered bad credentials : {}",dto.email() ,e.getMessage());
            throw e;
        }
    }

    @Transactional
    public String registerUser(SignUpDTO dto) {
        Member newMember = Member.builder()
                .email(dto.email())
                .firstName(dto.firstName())
                .lastName(dto.lastName())
                .phoneNumber(validatePhoneNumber(dto.phoneNumber()))
                .gender(Gender.valueOf(dto.gender()))
                .major(Major.valueOf(dto.major()))
                .academicYear(AcademicYear.valueOf(dto.academicYear()))
                .interests(dto.interests())
                .status(AccountStatus.DISABLED)
                .roles(Set.of(Role.USER))
                .password(passwordEncoder.encode(dto.password()))
                .build();

        memberRepository.save(newMember);
        log.info("user : {} has been registered", dto.email());
        sendWelcomeEmail(dto.email(), dto.lastName(), dto.firstName());
        return newMember.getEmail();
    }

    @Transactional
    public String activateUserAccount(Long id) {
        Member member = getUserById(id);

        if (member.getStatus() == AccountStatus.ACTIVATED) {
            log.info("Account {} is already activated", member.getEmail());
            return member.getEmail();
        }

        log.info("activating the account of : {}", member.getEmail());
        member.setStatus(AccountStatus.ACTIVATED);
        memberRepository.save(member);

        log.info("user : {} account has been activated", member.getEmail());

        sendActivationEmail(member.getEmail(), member.getLastName(), member.getFirstName());

        try {
            publishToRedis(new ParticipantDTO(member.getId(), member.getEmail()));
        } catch (Exception e) {
            log.error("Failed to publish user info to Redis Pub/Sub for user {}. The chat-service may not be available.", member.getEmail(), e);
        }

        log.info("user {} info published to Redis Pub/Sub ", member.getEmail());

        return member.getEmail();
    }

    @Transactional(readOnly = true)
    public Member getUserById(Long id) {
        return memberRepository.findMemberById((id))
                .orElseThrow(() -> new UserNotFoundException("user doesn't exist"));
    }

    @Transactional(readOnly = true)
    public Member getUserByEmail(String email) {
        return memberRepository.findMemberByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("user doesn't exist"));
    }

    public String getCurrentUser() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    private String validatePhoneNumber(String phoneNumber) {

        if (!PhoneNumberValidator.isValid(phoneNumber, "MA")) {
           throw new InvalidPhoneNumber("invalid phone number");
        }

        return PhoneNumberValidator.formatToE164(phoneNumber, "MA");
    }

    @Async
    public void sendActivationEmail(String email, String lastName, String firstName) {
        try {
            emailTemplateService.sendAccountActivationEmailNotification(email, lastName, firstName);
            log.debug("Activation email sent to {}", email);
        } catch (Exception e) {
            log.error("failed to send email to user : {}, due to : {}",email ,e.getMessage());
        }
    }

    @Async
    public void sendWelcomeEmail(String email, String lastName, String firstName) {
        try {
            emailTemplateService.sendWelcomeEmail(email, lastName, firstName);
            log.debug("Welcome email sent to {}", email);
        } catch (Exception e) {
            log.error("failed to send email to user : {}, due to : {}",email ,e.getMessage());
        }
    }

    @Async
    public void publishToRedis(ParticipantDTO msg) {
        redisPublisher.publish(msg);
    }

}
