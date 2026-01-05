package utils;

import java.io.InputStream;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Centralized configuration reader for test framework and application settings.
 * Loads properties from config.properties file.
 */
public final class ConfigReader {

    private static final Logger logger = LoggerFactory.getLogger(ConfigReader.class);
    private static final Properties PROPERTIES = new Properties();

    static {
        try (InputStream inputStream = ConfigReader.class
                .getClassLoader()
                .getResourceAsStream("config.properties")) {

            if (inputStream == null) {
                throw new RuntimeException("config.properties not found in classpath");
            }
            PROPERTIES.load(inputStream);
            logger.info("✓ Configuration loaded successfully");

        } catch (final Exception e) {
            final String message = "Failed to load config.properties";
            logger.error(message, e);
            throw new RuntimeException(message, e);
        }
    }

    private ConfigReader() {
        // Utility class - no instantiation
    }

    /**
     * Get a configuration value as String.
     *
     * @param key the configuration key
     * @return the configuration value, or null if not found
     */
    public static String get(final String key) {
        return PROPERTIES.getProperty(key);
    }

    /**
     * Get a configuration value as String with default.
     *
     * @param key          the configuration key
     * @param defaultValue the default value if key not found
     * @return the configuration value, or defaultValue if not found
     */
    public static String get(final String key, final String defaultValue) {
        return PROPERTIES.getProperty(key, defaultValue);
    }

    /**
     * Get a configuration value as boolean.
     *
     * @param key the configuration key
     * @return the boolean value, or false if not found
     */
    public static boolean getBoolean(final String key) {
        return Boolean.parseBoolean(get(key, "false"));
    }

    /**
     * Get a configuration value as boolean with default.
     *
     * @param key          the configuration key
     * @param defaultValue the default value if key not found
     * @return the boolean value
     */
    public static boolean getBoolean(final String key, final boolean defaultValue) {
        return Boolean.parseBoolean(get(key, String.valueOf(defaultValue)));
    }

    /**
     * Get a configuration value as integer.
     *
     * @param key the configuration key
     * @return the integer value
     */
    public static int getInt(final String key) {
        try {
            return Integer.parseInt(get(key, "0"));
        } catch (final NumberFormatException e) {
            logger.warn("Invalid integer value for key: {}", key, e);
            return 0;
        }
    }

    /**
     * Get a configuration value as integer with default.
     *
     * @param key          the configuration key
     * @param defaultValue the default value if key not found or invalid
     * @return the integer value
     */
    public static int getInt(final String key, final int defaultValue) {
        try {
            return Integer.parseInt(get(key, String.valueOf(defaultValue)));
        } catch (final NumberFormatException e) {
            logger.warn("Invalid integer value for key: {}, using default: {}", key, defaultValue, e);
            return defaultValue;
        }
    }

    /**
     * Get base URL from configuration.
     *
     * @return the base URL
     */
    public static String getBaseUrl() {
        return get("base.url");
    }

    /**
     * Check if a configuration key exists.
     *
     * @param key the configuration key
     * @return true if key exists, false otherwise
     */
    public static boolean hasKey(final String key) {
        return PROPERTIES.containsKey(key);
    }
}
