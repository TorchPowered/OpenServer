package net.openserver;

import net.openserver.config.ConfigurationManager;
import net.openserver.net.NetworkManager;

/**
 * Represents the main server component and representation.
 */
public class OpenServer {
    private static OpenServer openServer = null;

    private ConfigurationManager configManager;
    private NetworkManager networkManager;

    public NetworkManager getNetworkManager() {
        return networkManager;
    }

    public ConfigurationManager getConfigurationManager() {
        return configManager;
    }

    public static OpenServer getServer() {
        if(openServer == null) {
            openServer = new OpenServer();
        }
        return openServer;
    }
}
