package server.gameserver;

import codec.MsgPackDecoder;
import codec.MsgPackEncoder;
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
import io.netty.handler.timeout.IdleStateHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import server.*;

import java.util.concurrent.*;

/**
 * Created by zyl on /7/.
 */
public final class GameServer {

    public final static Logger logger = LoggerFactory.getLogger(GameServer.class);

    private static final int PORT = Integer.parseInt(System.getProperty("port", "8099"));

    volatile boolean gateWayConnected = false;

    private String internalGatewayIp = ServerSettings.getInstance().getProperty("gatewayInternalIp");
    private int internalGatewayPort = Integer.parseInt(ServerSettings.getInstance().getProperty("gatewayInternalPort"));
    private Integer networkUniqueId = NetworkUtility.networkUniqueId();

    private ExecutorService gameServerExecutorServicePool = new ThreadPoolExecutor(2, 2, 60, TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(), new ThreadFactory() {
        @Override
        public Thread newThread(Runnable r) {
            Thread t = new Thread(r);
            logger.debug("Created thread %d with name %s on %s\n", t.getId(), t.getName(),
                    this.getClass().getName());
            return t;
        }
    });


    public void run() {
        runInternalServerListenerTask();
        runClientListenerTask();
    }

    void runInternalServerListenerTask() {
        gameServerExecutorServicePool.execute(() -> {
            pollConnectGatewayServer();
        });
    }

    void runClientListenerTask(){
        gameServerExecutorServicePool.execute(() -> {
            runClientListener();
        });
    }

    private void runClientListener() {
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
                            p.addLast(new MsgPackDecoder());
                            p.addLast(new MsgPackEncoder());
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

    private void initInternalConnection() {
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
                            p.addLast(new LoggingHandler(LogLevel.INFO));
                            p.addLast(new IdleStateHandler(0, 5, 0));
                            p.addLast(new MsgPackDecoder());
                            p.addLast(new MsgPackEncoder());
                            p.addLast(new GameServerInternalMessageHandler(GameServer.this));
                        }
                    });

            // Start the client.
            ChannelFuture f = b.connect(internalGatewayIp, internalGatewayPort).sync();
            ServerBean gameSeraverBean = new ServerBean(networkUniqueId, ServerType.GameServer,
                    "gs-" + networkUniqueId, ServerStatus.Active,
                    f.channel().localAddress().toString(),
                    0);
            gameSeraverBean.store();
            logger.info("{}:{}Connect to gate way server {}:{} success.", f.channel().localAddress(),
                    0, internalGatewayIp, internalGatewayPort);
            // Wait until the connection is closed.
            f.channel().closeFuture().sync();
            gateWayConnected = true;
        } catch (InterruptedException e) {
            logger.debug("internal server Interrupted.");
        } finally {
            // Shut down the event loop to terminate all threads.
            group.shutdownGracefully();
            gateWayConnected = false;
        }
    }

    /**
     * 轮询GatewyServer活跃度做的连接
     */
    private void pollConnectGatewayServer() {
        while (!gateWayConnected) {
            try {
                logger.info("Trying to connect to gate way server {}:{}", internalGatewayIp, internalGatewayPort);
                Thread.sleep(5000);
                ServerStatus status = QueryServerStatus.getServerStatus(internalGatewayIp,
                        internalGatewayPort); //fixme: hard code now
                if (status == ServerStatus.Inavtive) {
                    continue;
                } else {
                    break;
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        //create connection;
        initInternalConnection();
    }

    public void close() {
        //todo
    }
}


