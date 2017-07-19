package server.gateway;


import base.messaging.binary.GameServerInitMsg;
import base.messaging.binary.PingMsg;
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
    public GatewayInternalServerMessageHandler(GatewayServer gatewayServer) {
        this.gatewayServer = gatewayServer;
    }

    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        gatewayServer.serverChannelManager.put(ctx.channel(), new Session(-1L, "UnInitialize", ctx.channel()));
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof PingMsg) {
            logger.info("receive heartbeat pack {}", msg);

            ctx.channel().writeAndFlush(msg);
        }
        if (msg instanceof GameServerInitMsg) { // server message;
            logger.info("receive heartbeat pack {}", msg);

            //init message
            //response pong;
            //ctx.channel().writeAndFlush("ok");
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
                    Session session = gatewayServer.serverChannelManager.get(ctx.channel());

                    if("UnInitialize".equals(session.getName())){
                        gatewayServer.serverChannelManager.remove(ctx.channel());
                        ctx.channel().close(); //还没有发送关闭初始化消息的服务器将直接关闭连接。
                    }
                    else{
                        session.onRemove();
                    }
                }
            }
        }
    }

    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.warn("GateWayInternalSeverMessageHandler.java {}", cause.toString());
        gatewayServer.safeClose();
    }
}
