package net.openserver.net.pipeline;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import net.openserver.meta.PacketCodec;
import net.openserver.meta.Pipeline;
import net.openserver.net.codec.Codec;
import net.openserver.util.ProtocolHeader;
import org.reflections.Reflections;

import java.util.Set;

/**
 * Handles all netty channel handling.
 */
@Pipeline
public class NetworkChannelHandler extends ChannelHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext context, Object msg) throws Exception {
        ByteBuf read = (ByteBuf) msg;
        ProtocolHeader header = ProtocolHeader.fromByteBuf(read);
        Reflections reflections = new Reflections("net.openserver");
        Set<Class<?>> codecs = reflections.getTypesAnnotatedWith(PacketCodec.class);
        for (Class codec : codecs) {
            PacketCodec annotation = (PacketCodec) codec.getAnnotation(PacketCodec.class);
            int opcode = annotation.opcode();
            if(opcode == header.id()) {
                Codec classCodec = (Codec) codec.newInstance();
                classCodec.handle(context, read);
                return;
            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext context, Throwable throwable) {
        throwable.printStackTrace();
    }
}
