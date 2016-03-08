package net.openserver.net.codec;

import com.google.common.base.Charsets;
import com.google.common.base.Throwables;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.openserver.OpenServer;
import net.openserver.meta.PacketCodec;
import net.openserver.util.ByteBufDecoders;

import java.io.ByteArrayOutputStream;
import java.math.BigInteger;
import java.net.InetSocketAddress;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Handles a situation when the server receives a encryption login response.
 */
@PacketCodec(opcode = 0x01)
public class EncryptionResponseCodec implements Codec {
    public void handle(ChannelHandlerContext context, ByteBuf read) throws Exception {
        InetSocketAddress address = (InetSocketAddress) context.channel().remoteAddress();
        OpenServer.getServer().logAsDebug("Channel (" + address.getAddress() + ":" + address.getPort() + ") has successfully received the encryption request and replied with a encryption response.");

        byte[] sharedSecret = new byte[ByteBufDecoders.readVarInt(read)];
        read.readBytes(sharedSecret);

        byte[] verifyToken = new byte[ByteBufDecoders.readVarInt(read)];
        read.readBytes(verifyToken);

    }

    public byte[] concat(byte[] arrayone, byte[] arraytwo) throws Exception {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        outputStream.write(arrayone);
        outputStream.write(arraytwo);
        return outputStream.toByteArray();
    }

    public String hash(String str) {
        try {
            byte[] digest = digest(str, "SHA-1");
            return new BigInteger(digest).toString(16);
        } catch (NoSuchAlgorithmException e) {
            throw Throwables.propagate(e);
        }
    }

    public byte[] digest(String str, String algorithm) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance(algorithm);
        byte[] strBytes = str.getBytes(Charsets.UTF_8);
        return md.digest(strBytes);
    }
}
