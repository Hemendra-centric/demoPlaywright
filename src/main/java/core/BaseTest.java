package core;

import com.microsoft.playwright.*;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(BrowserExtension.class)
public abstract class BaseTest {

    private Page page;
    private BrowserContext context;
    private String str = "";
    private String testurl = "dffffffffffff";

    public void DoStuff() {
    }
    // Lifecycle handled by BrowserExtension

    // Accessors to ensure fields are referenced (avoid static analysis unused-field
    // warnings)
    protected Page getPage() {
        return page;
    }

    protected BrowserContext getContext() {
        return context;
    }

    protected void setPage(final Page page) {
        this.page = page;
    }

    protected void setContext(final BrowserContext context) {
        this.context = context;
    }
}