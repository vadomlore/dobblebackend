package server.gateway;

import base.network.ChannelManager;
import base.network.Session;
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

import java.util.concurrent.*;

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

    ChannelManager serverChannelManager = new ChannelManager("channel-group-for-game-server");

    ChannelManager clientChannelManager = new ChannelManager("channel-group-for-client");

    private Channel knownChannelForClient = null;
    private Channel knownChannelForServer = null;


    public static ExecutorService gatewayexecutorpool = new ThreadPoolExecutor(2, 2, 60, TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(), new ThreadFactory() {
        @Override
        public Thread newThread(Runnable r) {
            Thread t = new Thread(r);
            loggger.info("run thread with name {} on {}", t.getId(), t.getName());
            return t;
        }
    });

    public void run() {
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
                                    new GatewayInternalServerMessageHandler(GatewayServer.this));
                        }
                    });
            ChannelFuture f = b.bind(port).sync();
            serverBean.setServerStatus(ServerStatus.Active);
            serverBean.store();
            knownChannelForServer = f.channel();
            f.channel().closeFuture().sync().addListener((e) -> {
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
            ChannelFuture f = b.bind(port).sync();
            knownChannelForClient = f.channel();
            f.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            loggger.debug("Gateway server bind InterruptedException. {}", e.toString());
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }


    public void safeClose() {

        //不让客户端进入
        if(this.knownChannelForClient != null){
            this.knownChannelForClient.close();
        }

        //不让新服务器进入
        if(this.knownChannelForServer != null){
            this.knownChannelForServer.close();
        }

        //关掉所有服务器连接
        CopyOnWriteArrayList<Session> ssessions =  this.serverChannelManager.getAll();
        for (Session session : ssessions ) {
            session.safeClose();
        }

        this.serverChannelManager.removeAll();

        //关掉所有客户端
        CopyOnWriteArrayList<Session> csessions =  this.clientChannelManager.getAll();
        for (Session session : csessions) {
            session.safeClose();
        }
        otherCleanUp();
    }

    void otherCleanUp(){
        //todo some clean up works here.
    }
}
