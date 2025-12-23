package ui;

import core.BaseTest;
import org.junit.jupiter.api.Test;
import utils.AccessibilityUtil;
import utils.ConfigReader;

public class AccessibilityTest extends BaseTest {

    @Test
    void homePageIsAccessible() {
        getPage().navigate(ConfigReader.getBaseUrl());
        AccessibilityUtil.scan(getPage(), "Home Page");
    }
}
