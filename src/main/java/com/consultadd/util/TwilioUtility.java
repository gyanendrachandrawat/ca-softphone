package com.consultadd.util;

public class TwilioUtility {
    public static boolean isPhoneNumber(String to) {
        return to.matches("^[\\d\\+\\-\\(\\) ]+$");
    }
}
