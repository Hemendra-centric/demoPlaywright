package utils;

import java.io.InputStream;
import java.util.Properties;

public final class ConfigReader {

    private static final Properties PROPERTIES = new Properties();

    static {
        try (InputStream inputStream = ConfigReader.class
                .getClassLoader()
                .getResourceAsStream("config.properties")) {

            if (inputStream == null) {
                throw new RuntimeException("config.properties not found in classpath");
            }
            PROPERTIES.load(inputStream);

        } catch (Exception e) {
            throw new RuntimeException("Failed to load config.properties", e);
        }
    }

    private ConfigReader() {
    }

    public static String get(final String key) {
        return PROPERTIES.getProperty(key);
    }

    public static boolean getBoolean(final String key) {
        return Boolean.parseBoolean(get(key));
    }

    public static int getInt(final String key) {
        return Integer.parseInt(get(key));
    }

    public static String getBaseUrl() {
        return get("base.url");
    }
}
