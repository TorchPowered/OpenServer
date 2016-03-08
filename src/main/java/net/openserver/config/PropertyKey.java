package net.openserver.config;

/**
 * Represents key names for properties inside of the configuration.
 */
public enum PropertyKey {
    MAX_PLAYERS("max-players", "10"),
    MOTD("motd", "A server running OpenServer (open-sourced server software)."),
    HOSTNAME("hostname", "localhost"),
    PORT("port", "25565");

    private String name;
    private String defaults;

    PropertyKey(String name, String defaults) {
        this.name = name;
        this.defaults = defaults;
    }

    public String getStringRepresentation() {
        return name;
    }

    public String getDefaultEntry() {
        return defaults;
    }
}
