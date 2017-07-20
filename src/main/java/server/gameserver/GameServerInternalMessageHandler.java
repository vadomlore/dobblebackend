package server.gameserver;

import base.messaging.binary.PingMsg;
import base.messaging.protobuf.ProtoBufferMessagePack;
import com.proto.gamename.Game;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by zyl on 2017/7/14.
 */
public class GameServerInternalMessageHandler extends ChannelInboundHandlerAdapter {

    public static Logger logger = LoggerFactory.getLogger(GameServerInternalMessageHandler.class);

    static final int GATE_WAY_MAX_IDLE_COUNT = 3;

    private int idleReadCounter = 0;

    private int heartBeatCountTest = 0;//fortest

    private GameServer gameServer;

    public GameServerInternalMessageHandler(GameServer gameServer) {
        this.gameServer = gameServer;
    }

    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        logger.info("game server {} connected to gateway server {}", ctx.channel().localAddress(),
                ctx.channel().remoteAddress());
        gameServer.setGatewayChannel(ctx.channel());

        logger.debug("send local ipaddress to remote");
        ProtoBufferMessagePack pack = new ProtoBufferMessagePack();
        pack.setSessionId(this.gameServer.getId());

        Game.ServerNode.Builder builder = Game.ServerNode.newBuilder();
        builder.setId(gameServer.getId());
        builder.setIp(gameServer.getIp());
        builder.setPort(gameServer.getPort());
        builder.setName(gameServer.getName());
        builder.setRole(Game.ServerNode.ServerRole.GameServer);
        pack.setMessageObject(builder.build());
        ctx.channel().writeAndFlush(pack);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if(msg instanceof PingMsg){
            new PingMsg();
            PingMsg pack = (PingMsg)msg;
            {
                logger.info("receive heart beat pack {}", pack);
            }
        }
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (IdleStateEvent.class.isAssignableFrom(evt.getClass())) {
            IdleStateEvent event = (IdleStateEvent) evt;
            if (event.state() == IdleState.READER_IDLE) {
                System.out.println("read idle");
                idleReadCounter++;
                if (idleReadCounter >= GATE_WAY_MAX_IDLE_COUNT) {
                    logger.warn("gate way server heart beat disappear");
                    onGatewayServerLostConnection(ctx.channel());
                }
            } else if (event.state() == IdleState.WRITER_IDLE) {
                System.out.println("write idle");
                if(heartBeatCountTest < 10){
                    //发送心跳到GatewayServer
                    PingMsg ping = new PingMsg(System.currentTimeMillis());
                    ping.setSessionId(this.gameServer.getId());
                    ctx.channel().writeAndFlush(ping);
                    heartBeatCountTest++;
                }

            } else if (event.state() == IdleState.ALL_IDLE)
                System.out.println("all idle");
        }
    }

    private void onGatewayServerLostConnection(Channel channel) {
        //TODO process disconnect;
        gameServer.close();

        //是否进行自动重连？
        //run the reconnect connect task thread;
        //gameServer.runInternalServerListenerTask();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error("exceptionCaught {}", cause.toString());
        if (ctx != null) {
            gameServer.close();
            logger.warn("disconnect with gateway server {}", ctx.channel().remoteAddress());
        }
    }
}