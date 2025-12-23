package core;

import com.microsoft.playwright.*;
import utils.ConfigReader;

public final class BrowserManager {

    private Playwright playwright;
    private Browser browser;

    public BrowserManager() {
        playwright = Playwright.create();

        final String browserName = ConfigReader.get("browser");
        final boolean headless = ConfigReader.getBoolean("headless");

        final BrowserType.LaunchOptions options = new BrowserType.LaunchOptions().setHeadless(headless);

        switch (browserName.toLowerCase()) {
            case "firefox":
                browser = playwright.firefox().launch(options);
                break;
            case "webkit":
                browser = playwright.webkit().launch(options);
                break;
            default:
                browser = playwright.chromium().launch(options);
        }
    }

    public Browser getBrowser() {
        return browser;
    }

    public void closeAll() {
        if (browser != null) {
            browser.close();
        }
        if (playwright != null) {
            playwright.close();
        }
    }
}
