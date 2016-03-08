package net.openserver.config;

import net.openserver.OpenServer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Properties;

/**
 * Represents the configuration manager to manage server configurations.
 */
public class ConfigurationManager {
    private final File configurationFile = new File(System.getProperty("user.dir"), "server.config");
    private final Properties properties = new Properties();

    private ConfigurationManager() {}

    public File getFile() {
        return configurationFile;
    }

    public String getProperty(PropertyKey propertyKey) {
        return properties.getProperty(propertyKey.getStringRepresentation(), propertyKey.getDefaultEntry());
    }

    public static ConfigurationManager init() throws Exception {
        if(!(OpenServer.getServer().getConfigurationManager() == null)) {
            return null;
        }
        ConfigurationManager configurationManager = new ConfigurationManager();
        if(!configurationManager.getFile().exists()) {
            configurationManager.getFile().createNewFile();
            for (PropertyKey key : PropertyKey.values()) {
                configurationManager.properties.setProperty(key.getStringRepresentation(), key.getDefaultEntry());
            }
            configurationManager.properties.store(new FileOutputStream(configurationManager.getFile()), "OpenServer Configuration");
            configurationManager.properties.load(new FileInputStream(configurationManager.getFile()));
        } else {
            configurationManager.properties.load(new FileInputStream(configurationManager.getFile()));
        }
        return configurationManager;
    }
}
