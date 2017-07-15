package server.gameserver;

import base.messaging.MessagePack;
import base.messaging.MessageRouteType;
import base.messaging.Ping;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.concurrent.EventExecutorGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import server.gateway.GatewayInternalServerMessageHandler;

/**
 * Created by zyl on 2017/7/14.
 */
public class GameServerInternalMessageHandler extends ChannelInboundHandlerAdapter {

    public static Logger logger = LoggerFactory.getLogger(GameServerInternalMessageHandler.class);

    static final int GATE_WAY_MAX_IDLE_COUNT = 3;

    private int idleReadCounter = 0;

    private GameServer gameServer;

    public GameServerInternalMessageHandler(GameServer gameServer) {
        this.gameServer = gameServer;
    }

    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        logger.info("game server {} connected to gateway server {}", ctx.channel().localAddress(),
                ctx.channel().remoteAddress());
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if(msg instanceof MessagePack){
            MessagePack pack = (MessagePack)msg;
            if(pack.msgType == 0){
                logger.info("receive heart beat pack {}", Ping.fromMessagePack(pack));
                //response pong;
                pack.routeType = MessageRouteType.GateToGameServer.value();
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
                //发送心跳到GatewayServer
                Ping ping = new Ping(MessageRouteType.GameServerToGate);
                ctx.channel().writeAndFlush(ping.getPack());
            } else if (event.state() == IdleState.ALL_IDLE)
                System.out.println("all idle");
        }
    }

    private void onGatewayServerLostConnection(Channel channel) {
        //TODO process disconnect;
        channel.close();

        //是否进行自动重连？
        //start the reconnect connect task thread;
        //gameServer.runInternalServerListenerTask();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error("exceptionCaught {}", cause.toString());
        if (ctx != null) {
            ctx.close();
            logger.warn("disconnect with gateway server {}", ctx.channel().remoteAddress());
        }
    }
}