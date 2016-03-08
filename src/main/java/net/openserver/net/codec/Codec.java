package net.openserver.net.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

/**
 * Represents a class that is a codec.
 */
public interface Codec {
    void handle(ChannelHandlerContext context, ByteBuf read) throws Exception ;
}
