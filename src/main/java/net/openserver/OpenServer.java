package net.openserver;

import net.openserver.config.ConfigurationManager;
import net.openserver.config.PropertyKey;
import net.openserver.net.NetworkManager;
import net.openserver.net.StatusResponse;
import net.openserver.net.codec.HandshakeCodec;
import net.openserver.net.status.MOTDData;
import net.openserver.net.status.PlayerCountData;
import net.openserver.net.status.VersionData;

import java.net.InetSocketAddress;
import java.security.KeyPair;
import java.security.KeyPairGenerator;

/**
 * Represents the main server component and representation.
 */
public class OpenServer {
    public static final int PROTOCOL_VERSION = 47;

    private static OpenServer openServer = null;

    private ConfigurationManager configManager;
    private NetworkManager networkManager;
    private KeyPair verificationKeyPair;

    private int onlinePlayersCount = 0;

    public NetworkManager getNetworkManager() {
        return networkManager;
    }

    public ConfigurationManager getConfigurationManager() {
        return configManager;
    }

    public static void main(String[] args) throws Exception {
        System.out.println("Starting configuration manager...");
        getServer().configManager = ConfigurationManager.init();
        System.out.println("Starting networking manager on: " + getServer().getConfigurationManager().getProperty(PropertyKey.HOSTNAME) + ":" + getServer().getConfigurationManager().getProperty(PropertyKey.PORT));
        String hostname = getServer().getConfigurationManager().getProperty(PropertyKey.HOSTNAME);
        int port = Integer.parseInt(getServer().getConfigurationManager().getProperty(PropertyKey.PORT));
        getServer().networkManager = NetworkManager.init(new InetSocketAddress(hostname, port));
        System.out.println("Binding to the hostname specified in the networking manager...");
        getServer().getNetworkManager().networking();
        System.out.println("Creating status response for clients...");
        // setup status response
        StatusResponse response = HandshakeCodec.SERVER_STATUS_RESPONSE;
        response.version = new VersionData();
        response.version.name = "OpenServer v" + PROTOCOL_VERSION;
        response.version.protocol = PROTOCOL_VERSION;
        response.players = new PlayerCountData();
        response.players.online = getServer().getOnlinePlayersCount();
        response.players.max = Integer.parseInt(getServer().getConfigurationManager().getProperty(PropertyKey.MAX_PLAYERS));
        response.description = new MOTDData();
        response.description.text = getServer().getConfigurationManager().getProperty(PropertyKey.MOTD);
        // end status response
        System.out.println("Generating 1024 RSA key pair...");
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(1024);
        getServer().verificationKeyPair = generator.genKeyPair();
        System.out.println("Server is accepting connections!");
        while (getServer().getNetworkManager().isRunning()) {

        }
    }

    public int getOnlinePlayersCount() {
        return onlinePlayersCount;
    }

    public KeyPair getKeyPair() {
        return this.verificationKeyPair;
    }

    public void logAsDebug(String msg) {
        System.out.println("[Minecraft][DEBUG] " + msg);
    }

    public static OpenServer getServer() {
        if(openServer == null) {
            openServer = new OpenServer();
        }
        return openServer;
    }
}
