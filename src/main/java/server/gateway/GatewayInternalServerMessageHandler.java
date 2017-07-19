package server.gateway;

import base.messaging.MessagePack;
import base.messaging.MessageRouteType;
import base.messaging.PingMsg;
import base.network.Session;
import io.netty.channel.*;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by zyl on 2017/7/14.
 */
public class GatewayInternalServerMessageHandler extends ChannelInboundHandlerAdapter {

    private GatewayServer gatewayServer;

    private int idleReadCounter = 0;

    static final int GATE_WAY_MAX_IDLE_COUNT = 3;

    static final Logger logger = LoggerFactory.getLogger(GatewayInternalServerMessageHandler.class);

    //简单的房间管理控制
    public GatewayInternalServerMessageHandler(GatewayServer gatewayServer){
        this.gatewayServer = gatewayServer;
    }

    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        gatewayServer.serverChannelManager.put(ctx.channel(), Session.dummySession);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if(msg instanceof MessagePack){
            MessagePack pack = (MessagePack)msg;
            if(pack.msgType == 0){ //heart beat
                logger.info("receive heartbeat pack {}", PingMsg.fromMessagePack(pack));
                //response pong;
                pack.routeType = MessageRouteType.GateToGameServer.value();
                ctx.channel().writeAndFlush(pack);
            }
            if(pack.msgType == 1){ // server message;
                logger.info("receive heartbeat pack {}", PingMsg.fromMessagePack(pack));
                //response pong;
                pack.routeType = MessageRouteType.GateToGameServer.value();
                ctx.channel().writeAndFlush(pack);
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
                    logger.warn("game server heart beat disappear", ctx.channel().remoteAddress());
                    //do some clean up

                    gatewayServer.serverChannelManager.remove(ctx.channel());
                }
            }
        }
    }

    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.warn("GateWayInternalSeverMessageHandler.java {}", cause.toString());
        gatewayServer.safeClose();
    }
}
