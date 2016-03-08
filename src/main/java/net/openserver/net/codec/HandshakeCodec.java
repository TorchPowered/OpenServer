package net.openserver.net.codec;

import com.google.gson.Gson;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import net.openserver.OpenServer;
import net.openserver.meta.PacketCodec;
import net.openserver.net.StatusResponse;
import net.openserver.util.ByteBufDecoders;

import java.net.InetSocketAddress;
import java.security.SecureRandom;
import java.security.interfaces.RSAPublicKey;

/**
 * Handles a situation when the server receives a handshake.
 */
@PacketCodec(opcode = 0x00)
public class HandshakeCodec implements Codec {
    public static final StatusResponse SERVER_STATUS_RESPONSE = new StatusResponse();

    public void handle(ChannelHandlerContext context, ByteBuf read) throws Exception {
        int protocolVersion = ByteBufDecoders.readVarInt(read);
        String clientAddress = ByteBufDecoders.readUTF8(read);
        int clientPort = read.readUnsignedShort();
        int nextState = ByteBufDecoders.readVarInt(read);
        InetSocketAddress address = (InetSocketAddress) context.channel().remoteAddress();

        if(!(protocolVersion == OpenServer.PROTOCOL_VERSION)) {
            OpenServer.getServer().logAsDebug("Channel (" + address.getAddress() + ":" + address.getPort() + ") sent a handshake packet with an incompatible protocol version.");
        }

        if(nextState == 2) {
            //login
            OpenServer.getServer().logAsDebug("Channel (" + address.getAddress() + ":" + address.getPort() + ") is attempting to login. (handshake packet with next state 2)");
            String name = ByteBufDecoders.readUTF8(read);
            // TODO whitelist operations
            // encryption operations
            String serverId = "";
            int publicKeyLength = ((RSAPublicKey) OpenServer.getServer().getKeyPair().getPublic()).getModulus().bitLength();
            byte[] publicKey = OpenServer.getServer().getKeyPair().getPublic().getEncoded();

            SecureRandom secureRandom = new SecureRandom();
            byte[] verifyToken = new byte[4];
            secureRandom.nextBytes(verifyToken);
            int tokenLength = verifyToken.length;

            ByteBuf packet = Unpooled.buffer();
            ByteBufDecoders.writeUTF8(packet, serverId);
            ByteBufDecoders.writeVarInt(packet, publicKeyLength);
            packet.writeBytes(publicKey);
            ByteBufDecoders.writeVarInt(packet, tokenLength);
            packet.writeBytes(verifyToken);

            ByteBuf finalPacket = ByteBufDecoders.writeHeader(0x01, packet);
            context.writeAndFlush(finalPacket);
            return;
        }

        Gson gson = new Gson();
        String statusResponse = gson.toJson(SERVER_STATUS_RESPONSE);

        ByteBuf byteBuf = Unpooled.buffer();
        ByteBufDecoders.writeUTF8(byteBuf, statusResponse);

        ByteBuf finalPacket = ByteBufDecoders.writeHeader(0x00, byteBuf);
        context.writeAndFlush(finalPacket);
    }
}
