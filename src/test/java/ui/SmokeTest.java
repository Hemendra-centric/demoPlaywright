package ui;

import core.BaseTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import utils.ConfigReader;

public class SmokeTest extends BaseTest {

    @Test
    void applicationLoadsSuccessfully() {
        getPage().navigate(ConfigReader.getBaseUrl());

        Assertions.assertTrue(
                getPage().title().length() > 0,
                "Page title should not be empty");
    }
}
