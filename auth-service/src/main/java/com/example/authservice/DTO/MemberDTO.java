package com.example.authservice.DTO;


public record MemberDTO(

        String email,

        String firstName,

        String lastName,

        String phoneNumber,

        String gender,

        String major,

        String academicYear
) {}
