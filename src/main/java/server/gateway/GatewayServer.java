package server.gateway;

import codec.MsgPackDecoder;
import codec.MsgPackEncoder;
import consts.ServerSettings;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import io.netty.handler.timeout.IdleStateHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import server.ServerBean;
import server.ServerStatus;
import server.ServerType;

/**
 * Created by zyl on 2017/7/14.
 */
public class GatewayServer {

    public static Logger loggger = LoggerFactory.getLogger(GatewayServer.class);

    private ServerBean serverBean;

    private Channel clientGateWayChannel = null;
    private Channel gateWayServerChannel = null;


    public static ExecutorService gatewayexecutorpool = new ThreadPoolExecutor(2, 2, 60, TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(), new ThreadFactory() {
        @Override
        public Thread newThread(Runnable r) {
            Thread t = new Thread(r);
            loggger.info("start thread with name {} on {}", t.getId(), t.getName());
            return t;
        }
    });

    public void start() {
        gatewayexecutorpool.execute(
                () -> {
                    startServerBind();
                });

        gatewayexecutorpool.execute(
                () -> {
                    startClientListener();
                }
        );
    }

    void startServerBind() {
        String ip = ServerSettings.getInstance().getProperty("gatewayInternalIp");
        int port = Integer.parseInt(ServerSettings.getInstance().getProperty("gatewayInternalPort"));
        NioEventLoopGroup bossGroup = new NioEventLoopGroup(1);
        NioEventLoopGroup workerGroup = new NioEventLoopGroup(1);

        //fixme: hard code
        serverBean = new ServerBean(1, ServerType.GateWayServer, "gateway-1",
                ServerStatus.Inavtive, ip, port);
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 100)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(
                                    new LoggingHandler(LogLevel.INFO),
                                    new IdleStateHandler(8, 0, 0),
                                    new MsgPackDecoder(),
                                    new MsgPackEncoder(),
                                    new GatewayInternalServerMessageHandler());
                        }
                    });
            ChannelFuture gateWayServerChannel = b.bind(port).sync();
            serverBean.setServerStatus(ServerStatus.Active);
            serverBean.store();
            gateWayServerChannel.channel().closeFuture().sync().addListener((e) -> {
                if (e.isSuccess()) {
                    serverBean.setServerStatus(ServerStatus.Inavtive);
                    serverBean.store();
                }
            });
        } catch (InterruptedException e) {
            loggger.debug("Gateway server bind InterruptedException. {}", e.toString());
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    void startClientListener() {
        String ip = ServerSettings.getInstance().getProperty("gatewayExternalIp");
        int port  = Integer.parseInt(ServerSettings.getInstance().getProperty("gatewayExternalPort"));
        NioEventLoopGroup bossGroup = new NioEventLoopGroup();
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 10000)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(
                                    new LoggingHandler(LogLevel.INFO),
                                    new ClientRequestHandler()); //处理客户端的连接请求
                        }
                    });
            ChannelFuture clientGateWayChannel = b.bind(port).sync();
            clientGateWayChannel.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            loggger.debug("Gateway server bind InterruptedException. {}", e.toString());
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    public void close() {
        this.clientGateWayChannel.close();
        this.gateWayServerChannel.close();
    }
}
