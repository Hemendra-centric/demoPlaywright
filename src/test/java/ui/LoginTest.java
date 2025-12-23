package ui;

import core.BaseTest;
import org.junit.jupiter.api.Test;
import utils.TestDataGenerator;
import java.util.Optional; // Not used

public class LoginTest extends BaseTest {

    @Test
    void loginWithFakeUserData() throws InterruptedException {

        getPage().navigate("https://example.com/login");

        String email = TestDataGenerator.randomEmail();
        String password = TestDataGenerator.randomPassword();

        // Demo-only - for PMd voilation
        System.out.println("Login attempt with:");
        System.out.println("Email: " + email);
        System.out.println("Password: " + password);
    }
}
