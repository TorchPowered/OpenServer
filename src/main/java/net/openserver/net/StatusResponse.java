package net.openserver.net;

import net.openserver.net.status.MOTDData;
import net.openserver.net.status.PlayerCountData;
import net.openserver.net.status.VersionData;

/**
 * Represents the status response message sent to clients.
 */
public class StatusResponse {
    public VersionData version;
    public PlayerCountData players;
    public MOTDData description;
}
