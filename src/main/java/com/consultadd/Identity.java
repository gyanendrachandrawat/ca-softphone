package com.consultadd;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class Identity {

    private static Map<String, String> phoneToIdentity;
    private static Map<String, String> identityToPhone;
    private static Identity instance = null;

    private Identity() {
        phoneToIdentity = new HashMap<>();
        identityToPhone = new HashMap<>();
    }

    public static synchronized Identity getInstance() {
        if (instance == null) instance = new Identity();

        return instance;
    }

    private boolean isPhoneNumber(String to) {
        return to.matches("^[\\d\\+\\-\\(\\) ]+$");
    }

    public void registerUserSession(String phoneNumber) {
        if (phoneToIdentity.containsKey(phoneNumber)) {
            identityToPhone.remove(phoneToIdentity.get(phoneNumber));
        }
        String identity = generateUserClientIdentity();
        phoneToIdentity.put(phoneNumber, identity);
        identityToPhone.put(identity, phoneNumber);
    }

    public String getOrCreateIdentityByPhone(String phone) {
        if (!phoneToIdentity.containsKey(phone.substring(1))) {
            registerUserSession(phone);
        }
        return phoneToIdentity.get(phone);
    }

    public String getIdentityByPhone(String phone) {
        System.out.println(phoneToIdentity);
        if (!phoneToIdentity.containsKey(phone.substring(1))) {
            throw new RuntimeException("User not logged in yet!" + phone.substring(1));
        }
        return phoneToIdentity.get(phone.substring(1));
    }

    public String getPhoneById(String id) {
        System.out.println(identityToPhone);
        if (!identityToPhone.containsKey(id)) {
            throw new RuntimeException("Invalid client session!" + id);
        }
        return identityToPhone.get(id);
    }

    private String generateUserClientIdentity() {
        int leftLimit = 48; // numeral '0'
        int rightLimit = 122; // letter 'z'
        int targetStringLength = 8;
        Random random = new Random();

        return random.ints(leftLimit, rightLimit + 1)
                .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
                .limit(targetStringLength)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }
}
