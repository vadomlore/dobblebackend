package server.gateway;


import base.messaging.binary.GameServerInitMsg;
import base.messaging.binary.PingMsg;
import base.messaging.protobuf.ProtoBufferMessagePack;
import base.network.GameServerSession;
import base.network.Session;
import com.proto.gamename.Game;
import io.netty.channel.*;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import server.ServerStatus;

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
        logger.info("Channel active {}", ctx);
        //gatewayServer.serverChannelManager.put(ctx.channel(), new GameServerSession(-1L, "UnInitialize", ctx.channel()));
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof PingMsg) {
            logger.info("receive heartbeat pack {}", msg);

            ctx.channel().writeAndFlush(msg);
        }

        if (msg instanceof GameServerInitMsg) { // server message;
            //init message
            //response pong;
            //ctx.channel().writeAndFlush("ok");
        }

        if(msg instanceof ProtoBufferMessagePack){
            ProtoBufferMessagePack pbMsgPack = (ProtoBufferMessagePack)msg;
            Object pbMsg = pbMsgPack.getMessageObject();

            if(pbMsg instanceof Game.ServerNode) {
                Game.ServerNode pbMessage = (Game.ServerNode)pbMsg;
                logger.info("receive heartbeat pack {}", msg);
                GameServerSession gsSession = new GameServerSession(pbMessage.getId(), pbMessage.getName(), ctx.channel());
                gsSession.setNode(pbMessage);
                gsSession.setStatus(ServerStatus.Active);
                gatewayServer.serverChannelManager.put(ctx.channel(), gsSession);
                logger.info("{} registered.", pbMsg);
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
                    Session session = gatewayServer.serverChannelManager.get(ctx.channel());
                    if(session != null){
                        gatewayServer.serverChannelManager.remove(ctx.channel());
                    }
                    else{
                        ctx.channel().close();//还没有发送关闭初始化消息的服务器将直接关闭连接。
                    }
                }
            }
        }
    }

    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.warn("GateWayInternalSeverMessageHandler.java {}", cause.toString());
        gatewayServer.serverChannelManager.remove(ctx.channel());
    }
}
