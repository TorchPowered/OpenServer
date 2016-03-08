package net.openserver.net;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import net.openserver.OpenServer;
import net.openserver.meta.Pipeline;
import org.reflections.Reflections;

import java.net.InetSocketAddress;
import java.util.Set;

/**
 * Manages all network related items.
 */
public class NetworkManager {
    private InetSocketAddress serverAddress;

    private EventLoopGroup bossGroup = new NioEventLoopGroup();
    private EventLoopGroup workerGroup = new NioEventLoopGroup();
    private ServerBootstrap networkBootstrap;

    private ChannelFuture future;

    private boolean isRunning = false;

    private NetworkManager(InetSocketAddress address) { this.serverAddress = address; }

    public static NetworkManager init(InetSocketAddress address) {
        if(!(OpenServer.getServer().getNetworkManager() == null)) {
            return null;
        }
        return new NetworkManager(address);
    }

    public void networking() throws Exception {
        if(isRunning) {
            return;
        }
        networkBootstrap = new ServerBootstrap()
                .group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        Reflections reflections = new Reflections("net.openserver");
                        Set<Class<?>> pipelineClasses = reflections.getTypesAnnotatedWith(Pipeline.class);
                        for (Class<?> pipelineClass : pipelineClasses) {
                            Object classInstance = pipelineClass.newInstance();
                            if(ChannelHandler.class.isAssignableFrom(pipelineClass)) {
                                ChannelHandler handler = (ChannelHandler) classInstance;
                                socketChannel.pipeline().addLast(handler);
                            }
                        }
                    }
                });
        future = networkBootstrap.bind(serverAddress.getAddress(), serverAddress.getPort()).sync();
        isRunning = true;
    }

    public boolean isRunning() {
        return isRunning;
    }
}
