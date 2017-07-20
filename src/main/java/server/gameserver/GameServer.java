package server.gameserver;

import base.utility.SnowflakeIdWorker;
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
import sun.net.util.IPAddressUtil;

import java.util.Scanner;
import java.util.UUID;
import java.util.concurrent.*;

/**
 * Created by zyl on /7/.
 */
public final class GameServer {

    public final static Logger logger = LoggerFactory.getLogger(GameServer.class);

    private static final int PORT = Integer.parseInt(System.getProperty("port", "8091"));

    private String ip;

    private int port;

    private String name;

    private long id;

    GameServer(){
        this.id = new SnowflakeIdWorker(0, 0).nextId();
        this.name = "default-game-server";
        this.ip = "127.0.0.1";
        this.port = PORT;
    }

    GameServer(String ip, int port) {
        this("default", ip, port);
    }


    GameServer(String name, String ip, int port){
        this.id = new SnowflakeIdWorker(0, 0).nextId();
        this.name = name;
        this.ip = ip;
        this.port = port;
    }

    volatile boolean gateWayConnected = false;

    volatile Channel channleForClient;

    volatile Channel channelForGateway;

    private String internalGatewayIp = ServerSettings.getInstance().getProperty("gatewayInternalIp");
    private int internalGatewayPort = Integer.parseInt(ServerSettings.getInstance().getProperty("gatewayInternalPort"));
    private Integer networkUniqueId = NetworkUtility.networkUniqueId();

    public void setGatewayChannel(Channel channel){
        this.channelForGateway = channel;
    }

    private ExecutorService gameServerExecutorServicePool = new ThreadPoolExecutor(2, 2, 60, TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(), new ThreadFactory() {
        @Override
        public Thread newThread(Runnable r) {
            Thread t = new Thread(r);
            logger.debug("Created thread %d with name %s on %s\n", t.getId(), t.getName(), this.getClass().getName());
            return t;
        }
    });


    public void run() {
        runInternalServerListenerTask();
        runClientListenerTask();
        System.out.println("Wait for game server to exit");
        Scanner scanner = new Scanner(System.in);
        String line = scanner.next();
        close();
    }

    void runInternalServerListenerTask() {
        gameServerExecutorServicePool.execute(() -> {
            pollConnectGatewayServer();
        });
    }

    void runClientListenerTask() {
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
            final ChannelFuture f = b.bind(port).sync();
            channleForClient = f.channel();

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
                            //p.addLast(new IdleStateHandler(0, 5, 0));
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

            logger.error("game server future channel is {} .", f.channel());
            f.channel().closeFuture().sync();
            gateWayConnected = true;
        } catch (InterruptedException e) {
            logger.debug("GameServer internal connection Interrupted.");
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
        if (channelForGateway != null) {
            channelForGateway.close();
        }
        if (channleForClient != null) {
            channleForClient.close();
        }

    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}


