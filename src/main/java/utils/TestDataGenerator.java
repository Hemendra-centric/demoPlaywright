package utils;

import java.util.UUID;

public final class TestDataGenerator {

    private TestDataGenerator() {
    }

    public static String randomEmail() {
        return "user_" + UUID.randomUUID() + "@test.com";
    }

    public static String randomPassword() {
        return "Pwd@" + UUID.randomUUID().toString().substring(0, 6);
    }

    public static String randomUsername() {
        return "user_" + UUID.randomUUID().toString().substring(0, 5);
    }
}
