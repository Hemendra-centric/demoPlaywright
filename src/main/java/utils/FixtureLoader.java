package utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utilities for loading and managing test fixtures and mock data.
 * Supports JSON-based fixtures for API mocking and test data.
 */
public final class FixtureLoader {

    private static final Logger logger = LoggerFactory.getLogger(FixtureLoader.class);
    private static final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .serializeNulls()
            .create();
    private static final String FIXTURES_PATH = "src/test/resources/fixtures";

    private FixtureLoader() {
        // Utility class - no instantiation
    }

    /**
     * Load a JSON fixture file and parse it into an object.
     *
     * @param <T>      the type to deserialize to
     * @param fileName the fixture file name (without path)
     * @param classOfT the class to deserialize to
     * @return the deserialized object
     */
    public static <T> T loadFixture(final String fileName, final Class<T> classOfT) {
        return loadFixture(fileName, classOfT, "");
    }

    /**
     * Load a JSON fixture file from a specific subfolder.
     *
     * @param <T>       the type to deserialize to
     * @param fileName  the fixture file name (without path)
     * @param classOfT  the class to deserialize to
     * @param subfolder the subfolder within fixtures directory
     * @return the deserialized object
     */
    public static <T> T loadFixture(final String fileName, final Class<T> classOfT, final String subfolder) {
        try {
            final String path = subfolder.isEmpty()
                    ? String.format("%s/%s", FIXTURES_PATH, fileName)
                    : String.format("%s/%s/%s", FIXTURES_PATH, subfolder, fileName);

            final Path filePath = Paths.get(path);

            if (!Files.exists(filePath)) {
                final String message = String.format("Fixture file not found: %s", path);
                logger.error(message);
                throw new IllegalArgumentException(message);
            }

            final String content = new String(Files.readAllBytes(filePath), StandardCharsets.UTF_8);
            logger.debug("✓ Loaded fixture: {}", path);

            return gson.fromJson(content, classOfT);
        } catch (final Exception e) {
            logger.error("Failed to load fixture: {}", fileName, e);
            throw new RuntimeException("Failed to load fixture: " + fileName, e);
        }
    }

    /**
     * Load raw JSON fixture as String.
     *
     * @param fileName the fixture file name
     * @return the raw JSON content
     */
    public static String loadFixtureAsString(final String fileName) {
        return loadFixtureAsString(fileName, "");
    }

    /**
     * Load raw JSON fixture as String from subfolder.
     *
     * @param fileName  the fixture file name
     * @param subfolder the subfolder within fixtures directory
     * @return the raw JSON content
     */
    public static String loadFixtureAsString(final String fileName, final String subfolder) {
        try {
            final String path = subfolder.isEmpty()
                    ? String.format("%s/%s", FIXTURES_PATH, fileName)
                    : String.format("%s/%s/%s", FIXTURES_PATH, subfolder, fileName);

            final Path filePath = Paths.get(path);

            if (!Files.exists(filePath)) {
                final String message = String.format("Fixture file not found: %s", path);
                logger.error(message);
                throw new IllegalArgumentException(message);
            }

            final String content = new String(Files.readAllBytes(filePath), StandardCharsets.UTF_8);
            logger.debug("✓ Loaded fixture: {}", path);

            return content;
        } catch (final Exception e) {
            logger.error("Failed to load fixture: {}", fileName, e);
            throw new RuntimeException("Failed to load fixture: " + fileName, e);
        }
    }

    /**
     * Convert an object to JSON string.
     *
     * @param object the object to serialize
     * @return the JSON representation
     */
    public static String toJson(final Object object) {
        return gson.toJson(object);
    }

    /**
     * Parse JSON string to an object.
     *
     * @param <T>      the type to deserialize to
     * @param json     the JSON string
     * @param classOfT the class to deserialize to
     * @return the deserialized object
     */
    public static <T> T fromJson(final String json, final Class<T> classOfT) {
        return gson.fromJson(json, classOfT);
    }

    /**
     * Check if a fixture file exists.
     *
     * @param fileName the fixture file name
     * @return true if fixture exists, false otherwise
     */
    public static boolean fixtureExists(final String fileName) {
        return fixtureExists(fileName, "");
    }

    /**
     * Check if a fixture file exists in subfolder.
     *
     * @param fileName  the fixture file name
     * @param subfolder the subfolder within fixtures directory
     * @return true if fixture exists, false otherwise
     */
    public static boolean fixtureExists(final String fileName, final String subfolder) {
        final String path = subfolder.isEmpty()
                ? String.format("%s/%s", FIXTURES_PATH, fileName)
                : String.format("%s/%s/%s", FIXTURES_PATH, subfolder, fileName);

        return Files.exists(Paths.get(path));
    }

    /**
     * Get Gson instance for advanced operations.
     *
     * @return the Gson instance
     */
    public static Gson getGson() {
        return gson;
    }
}
