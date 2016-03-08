package net.openserver.util;

import io.netty.buffer.ByteBuf;

/**
 * Represents a packet protocol header.
 */
public class ProtocolHeader {
    private int packetSize;
    private int packetId;

    private ProtocolHeader(ByteBuf byteBuf) throws Exception { this.packetSize = ByteBufDecoders.readVarInt(byteBuf); this.packetId = ByteBufDecoders.readVarInt(byteBuf); }

    public static ProtocolHeader fromByteBuf(ByteBuf byteBuf) throws Exception {
        return new ProtocolHeader(byteBuf);
    }

    public int size() {
        return packetSize;
    }

    public int id() {
        return packetId;
    }
}
