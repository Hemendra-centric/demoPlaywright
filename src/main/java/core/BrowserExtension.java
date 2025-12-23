package core;

import com.microsoft.playwright.*;
import org.junit.jupiter.api.extension.*;
import utils.ConfigReader;

public class BrowserExtension implements BeforeAllCallback, AfterAllCallback, BeforeEachCallback, AfterEachCallback {

    private static BrowserManager browserManager;

    @Override
    public void beforeAll(final ExtensionContext extensionContext) throws Exception {
        if (browserManager == null) {
            browserManager = new BrowserManager();
        }
    }

    @Override
    public void afterAll(final ExtensionContext extensionContext) throws Exception {
        if (browserManager != null) {
            browserManager.closeAll();
            browserManager = null;
        }
    }

    @Override
    public void beforeEach(final ExtensionContext extensionContext) throws Exception {
        final Object testInstance = extensionContext.getRequiredTestInstance();
        if (!(testInstance instanceof BaseTest)) {
            return;
        }

        final BaseTest test = (BaseTest) testInstance;
        final Browser browser = browserManager.getBrowser();
        final BrowserContext context = browser.newContext();
        final Page page = context.newPage();
        page.setDefaultTimeout(ConfigReader.getInt("timeout"));

        test.setContext(context);
        test.setPage(page);
    }

    @Override
    public void afterEach(final ExtensionContext extensionContext) throws Exception {
        final Object testInstance = extensionContext.getRequiredTestInstance();
        if (!(testInstance instanceof BaseTest)) {
            return;
        }

        final BaseTest test = (BaseTest) testInstance;
        final BrowserContext context = test.getContext();
        if (context != null) {
            context.close();
        }
        test.setContext(null);
        test.setPage(null);
    }
}