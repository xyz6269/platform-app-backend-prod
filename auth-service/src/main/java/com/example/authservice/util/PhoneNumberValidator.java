package com.example.authservice.util;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;

public class PhoneNumberValidator {

    private static final PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();

    public static boolean isValid(String number, String regionCode) {
        try {
            Phonenumber.PhoneNumber phoneNumber = phoneUtil.parse(number, regionCode);
            return phoneUtil.isValidNumber(phoneNumber);
        } catch (NumberParseException e) {
            return false;
        }
    }

    public static String formatToE164(String number, String regionCode) {
        try {
            Phonenumber.PhoneNumber phoneNumber = phoneUtil.parse(number, regionCode);
            return phoneUtil.format(phoneNumber, PhoneNumberUtil.PhoneNumberFormat.E164);
        } catch (NumberParseException e) {
            throw new IllegalArgumentException("Invalid phone number: " + number);
        }
    }
}
