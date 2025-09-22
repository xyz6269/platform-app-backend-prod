package com.example.authservice.mail;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailTemplateService {

    private final EmailService emailService;

    @Value("${link.whatsapp}")
    private String invitationLink;

    // Base HTML template with INLINE styles (email-safe)
    private String getBaseTemplate(String title, String greeting, String message, String ctaText, String ctaLink) {
        return """
            <!DOCTYPE html>
            <html>
            <body style="margin:0;padding:0;background-color:#2a2a2a;font-family:Arial, sans-serif;">
              <table role="presentation" width="100%%" cellspacing="0" cellpadding="0" border="0" style="background-color:#2a2a2a;padding:20px 0;">
                <tr>
                  <td align="center">
                    <table role="presentation" width="600" cellspacing="0" cellpadding="0" border="0" style="background-color:#333333;border-radius:12px;padding:40px;border:1px solid #444;">
                      <tr>
                        <td align="center" style="padding-bottom:30px;">
                          <img src="cid:AIS.png" width="140" height="140" style="display:block;border-radius:50%%;">
                        </td>
                      </tr>
                      <tr>
                         <td align="center" style="background-color:#0066CC;color:#ffffff;font-size:12px;font-weight:600;letter-spacing:2px;border:1px solid #666;padding:8px 16px;border-radius:6px;">
                            APP IN SCIENCE PLATFORM
                         </td>
                      </tr>
                      <tr>
                        <td align="center" style="color:#ffffff;font-size:28px;font-weight:bold;padding:30px 0 20px;">
                          %s
                        </td>
                      </tr>
                      <tr>
                        <td align="center" style="padding-bottom:20px;">
                          <hr style="border:0;border-top:1px solid #666;width:80px;">
                        </td>
                      </tr>
                      <tr>
                        <td align="center" style="color:#ffffff;font-size:18px;font-weight:500;padding-bottom:20px;">
                          %s
                        </td>
                      </tr>
                      <tr>
                        <td style="color:#ccc;font-size:16px;line-height:1.6;text-align:left;padding-bottom:30px;">
                          %s
                        </td>
                      </tr>
                      <tr>
                        <td align="center">
                          %s
                        </td>
                      </tr>
                      <tr>
                        <td align="center" style="color:#ccc;font-size:14px;padding-top:40px;">
                          Best regards,<br>
                          <span style="color:#fff;font-weight:500;">AIS Team</span>
                        </td>
                      </tr>
                    </table>
                  </td>
                </tr>
              </table>
            </body>
            </html>
            """.formatted(title, greeting, message, ctaText != null ? getCTAButton(ctaText, ctaLink) : "");
    }

    // CTA Button with inline styling
    private String getCTAButton(String text, String link) {
        if (text == null || link == null) return "";
        return """
            <a href="%s" style="background-color:#25D366;color:#ffffff;text-decoration:none;padding:16px 32px;border-radius:8px;font-size:16px;font-weight:600;display:inline-block;">
              %s
            </a>
            """.formatted(link, text);
    }

    // Welcome Email
    public void sendWelcomeEmail(String email, String lastName, String firstName) {
        String title = "Welcome to App in Science Platform!";
        String greeting = "Dear " + lastName + " " + firstName + ",";
        String message = """
            We are delighted to inform you that your registration has been successful!<br><br>
            Our team will carefully review your profile and keep you updated with our decision.<br><br>
            We appreciate your interest in joining our club.
            """;
        String htmlContent = getBaseTemplate(title, greeting, message, null, "");
        emailService.setMailSender(email, title, htmlContent);
    }

    // Account Activation Email
    public void sendAccountActivationEmailNotification(String email, String lastName, String firstName) {
        String title = "Congratulations! You've been accepted in the Club";
        String greeting = "Dear " + lastName + " " + firstName + ",";
        String message = """
            We are delighted to inform you that you're now an active member of our club.<br><br>
            Your account is active, and you can start exploring all features we have to offer.<br><br>
            If you have any questions, don't hesitate to reach out to our support team.
            """;
        String htmlContent = getBaseTemplate(title, greeting, message, "join the whatsapp group", invitationLink);
        emailService.setMailSender(email, title, htmlContent);
    }
}
