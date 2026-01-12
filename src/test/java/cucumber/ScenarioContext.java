package cucumber;

import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Page;
import com.google.gson.JsonObject;

/**
 * Shared context to pass Page and BrowserContext between Cucumber hooks and
 * step definitions.
 * Uses thread-local storage to ensure isolation between parallel test threads.
 */
public class ScenarioContext {

    private static final ThreadLocal<Page> PAGE = new ThreadLocal<>();
    private static final ThreadLocal<BrowserContext> CONTEXT = new ThreadLocal<>();
    private static final ThreadLocal<JsonObject> API_REQUEST = new ThreadLocal<>();
    private static final ThreadLocal<JsonObject> API_RESPONSE = new ThreadLocal<>();
    private static final ThreadLocal<Integer> API_RESPONSE_STATUS = new ThreadLocal<>();

    public static void setPage(final Page page) {
        PAGE.set(page);
    }

    public static Page getPage() {
        return PAGE.get();
    }

    public static void setContext(final BrowserContext context) {
        CONTEXT.set(context);
    }

    public static BrowserContext getContext() {
        return CONTEXT.get();
    }

    public static void setApiRequest(final JsonObject request) {
        API_REQUEST.set(request);
    }

    public static JsonObject getApiRequest() {
        return API_REQUEST.get();
    }

    public static void setApiResponse(final JsonObject response) {
        API_RESPONSE.set(response);
    }

    public static JsonObject getApiResponse() {
        return API_RESPONSE.get();
    }

    public static void setApiResponseStatus(final int status) {
        API_RESPONSE_STATUS.set(status);
    }

    public static Integer getApiResponseStatus() {
        return API_RESPONSE_STATUS.get();
    }

    public static void cleanup() {
        PAGE.remove();
        CONTEXT.remove();
        API_REQUEST.remove();
        API_RESPONSE.remove();
        API_RESPONSE_STATUS.remove();
    }
}
