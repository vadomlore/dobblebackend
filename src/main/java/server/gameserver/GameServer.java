package server.gameserver;

import consts.ServerSettings;
import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import server.QueryServerStatus;
import server.ServerStatus;

/**
 * Created by zyl on /7/.
 */
public final class GameServer {

    public final static Logger logger = LoggerFactory.getLogger(GameServer.class);

    static final int PORT = Integer.parseInt(System.getProperty("port", ""));

    static volatile Channel internalChannel = null;

    void run() {
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 0)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline p = ch.pipeline();
                            p.addLast(new LoggingHandler(LogLevel.INFO));
                            p.addLast(new GameServerHandler());
                        }
                    });

            // Start the server.
            final ChannelFuture f = b.bind(PORT).sync();

            // Wait until the server socket is closed.
            f.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            logger.debug("Game server interrupted");
        } finally {
            // Shut down all event loops to terminate all threads.
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }


    void initInternalConnection() {
        // Configure the client. 
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(group)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline p = ch.pipeline();
                            //p.addLast(new LoggingHandler(LogLevel.INFO));
                            p.addLast(new GameServerInternalMessageHandler());
                        }
                    });

            // Start the client.
            ChannelFuture f = b.connect(ServerSettings.getProperty("gatewayInternalIp"),
                    Integer.parseInt(ServerSettings.getProperty("gatewayInternalPort"))).sync();
            // Wait until the connection is closed.
            f.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            logger.debug("internal server Interrupted.");
        } finally {
            // Shut down the event loop to terminate all threads.
            group.shutdownGracefully();
        }
    }

    /**
     * 轮询GameServer活跃度做的连接
     */
    public void pollConnectGatewayServer() {
        while (true) {
            try {
                Thread.sleep(5000);
                ServerStatus status = QueryServerStatus.getServerStatus(ServerSettings.getProperty("gatewayInternalIp"),
                        Integer.parseInt(ServerSettings.getProperty("gatewayInternalPort"))); //fixme: hard code now
                if (status == ServerStatus.Inavtive) {
                    continue;
                } else {
                    //create connection;
                    initInternalConnection();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}


