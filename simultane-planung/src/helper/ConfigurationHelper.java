package helper;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Properties;

/**
 * Helper methods for application configuration.
 */
public class ConfigurationHelper {
    /**
     * Singleton instance.
     */
    private final static ConfigurationHelper instance = new ConfigurationHelper();

    /**
     * Returns the singleton instance.
     *
     * @return Singleton instance
     */
    public static ConfigurationHelper getInstance() {
        return ConfigurationHelper.instance;
    }

    /**
     * Private constructor to avoid bypassing singleton.
     */
    private ConfigurationHelper() {
        try {
            loadConfiguration();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Properties instance.
     */
    private Properties configuration = null;

    /**
     * Loads config.properties an instantiate the Properties object.
     * @throws Exception If instance already set.
     */
    private void loadConfiguration() throws Exception {
        if (configuration == null) {
            try {
                FileInputStream in = new FileInputStream("config.properties");
                configuration = new Properties();
                configuration.load(in);
                in.close();
            } catch (FileNotFoundException e) {
                // No config.properties available, create copy from
                // config.properties.default and reload.
                FilesystemHelper.getInstance().copyFile("config.properties.default", "config.properties");
                loadConfiguration();
            }
        } else {
            throw new Exception("Properties instance already available.");
        }
    }

    /**
     * Returns a configuration property for a specific key.
     * @param key Configuration key.
     * @return Configuration property for key with fallback.
     */
    public String getProperty(String key) {
        return configuration.getProperty(key);
    }

    /**
     * Returns a configuration property for a specific key.
     * @param key Configuration key.
     * @param fallback Fallback value.
     * @return Configuration property for key with fallback.
     */
    public String getProperty(String key, String fallback) {
        return configuration.getProperty(key, fallback);
    }

    /**
     * Returns a configuration property as boolean.
     * @param key Configuration key.
     * @return Configuration property.
     */
    public boolean getPropertyBoolean(String key) {
        String booleanValue = configuration.getProperty(key);
        if (booleanValue == null || booleanValue.trim().equals("")) {
            return false;
        }

        return Boolean.valueOf(configuration.getProperty(key));
    }

    /**
     * Returns a configuration property as integer.
     * @param key Configuration key.
     * @param fallback Fallback value.
     * @return Configuration property for key with fallback.
     */
    public int getPropertyInteger(String key, int fallback) {
        String intNumber = configuration.getProperty(key);
        if (intNumber == null || intNumber.trim().equals("")) {
            return fallback;
        }

        return Integer.valueOf(configuration.getProperty(key));
    }

    /**
     * Returns a configuration property as long.
     * @param key Configuration key.
     * @param fallback Fallback value.
     * @return Configuration property for key with fallback.
     */
    public long getPropertyLong(String key, long fallback) {
        String longNumber = configuration.getProperty(key);
        if (longNumber == null || longNumber.trim().equals("")) {
            return fallback;
        }

        return Long.getLong(configuration.getProperty(key));
    }

    /**
     * Returns an array of strings.
     * @param key Configuration key
     * @param separator Separator
     * @return Array of strings
     */
    public String[] getPropertyArray(String key, String separator) {
        String values = getProperty(key);
        return values.split(separator);
    }

    /**
     * Returns an array of strings.
     * @param key Configuration key
     * @return Array of strings
     */
    public String[] getPropertyArray(String key) {
        return getPropertyArray(key, "\\,");
    }
}

