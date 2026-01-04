package ui;

import core.BaseTest;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

import utils.AccessibilityUtil;
import utils.ConfigReader;

public class AccessibilityTest extends BaseTest {

    @Test
    void homePageIsAccessible() {
        getPage().navigate(ConfigReader.getBaseUrl());
        AccessibilityUtil.scan(getPage(), "Home Page");
    }
}
