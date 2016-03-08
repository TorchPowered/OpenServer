package net.openserver;

import net.openserver.config.ConfigurationManager;

/**
 * Represents the main server component and representation.
 */
public class OpenServer {
    private static OpenServer openServer = null;

    private ConfigurationManager configManager;

    public static OpenServer getServer() {
        if(openServer == null) {
            openServer = new OpenServer();
        }
        return openServer;
    }
}
