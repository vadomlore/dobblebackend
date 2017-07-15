package server.gateway;

import base.messaging.MessagePack;
import base.messaging.MessageRouteType;
import base.messaging.Ping;
import io.netty.channel.*;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.concurrent.EventExecutorGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.plugin2.message.Message;

/**
 * Created by zyl on 2017/7/14.
 */
public class GatewayInternalServerMessageHandler extends ChannelInboundHandlerAdapter {
    //简单的房间管理控制
    static final int GATE_WAY_MAX_IDLE_COUNT = 3;

    private int idleReadCounter = 0;

    static final Logger logger = LoggerFactory.getLogger(GatewayInternalServerMessageHandler.class);
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if(msg instanceof MessagePack){
            MessagePack pack = (MessagePack)msg;
            if(pack.msgType == 0){
                logger.info("receive heart beat pack {}", Ping.fromMessagePack(pack));
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
                    cleanGameServerConnection(ctx.channel());
                }
            }
        }
    }

    void cleanGameServerConnection(Channel channel){
        if(channel != null){
            channel.close();
        }
        //todo do some clean up game logic related;
    }
}
