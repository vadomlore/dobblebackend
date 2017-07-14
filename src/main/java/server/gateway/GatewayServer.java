package server.gateway;

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

    private  Channel clientGateWayChannel = null;
    private Channel gateWayServerChannel = null;


    private ExecutorService service = new ThreadPoolExecutor(2, 2, 60, TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(), new ThreadFactory() {
        @Override
        public Thread newThread(Runnable r) {
            Thread t = new Thread(r);
            loggger.debug("Created thread %d with name %s on %s\n", t.getId(), t.getName(),
                    this.getClass().getName());
            return t;
        }
    });

    public void start() {
        service.execute(
                () -> {
                    startServerBind(ServerSettings.getProperty("gatewayInternalIp"),
                            Integer.parseInt(ServerSettings.getProperty("gatewayInternalPort")));
                });

        service.execute(
                () -> {
                    startClientListener(ServerSettings.getProperty("gatewayExternalIp"),
                            Integer.parseInt(ServerSettings.getProperty("gatewayExternalPort")));
                }
        );
    }

    private void startServerBind(String ip, int port) {
        NioEventLoopGroup bossGroup = new NioEventLoopGroup(1);
        NioEventLoopGroup workerGroup = new NioEventLoopGroup(1);

        //fixme: hard code
        serverBean = new ServerBean(1, ServerType.GateWayServer, "gs-" + serverBean.getServerUniqueId(),
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
                                    new GatewayInternalServerMessageHandler());
                        }
                    });
            ChannelFuture gateWayServerChannel = b.bind(port).sync();
            serverBean.setServerStatus(ServerStatus.Active);
            serverBean.store();
            gateWayServerChannel.channel().closeFuture().sync().addListener((e)->{
                if(e.isSuccess()){
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


    private void startClientListener(String ip, int port) {
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
                                    //new LoggingHandler(LogLevel.INFO),
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

    public void close(){
        this.clientGateWayChannel.close();
        this.gateWayServerChannel.close();
    }
}
