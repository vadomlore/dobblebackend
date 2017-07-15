package codec;

import base.messaging.MessagePack;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * Created by Administrator on 2017/7/15.
 */
public class MsgPackEncoder extends MessageToByteEncoder {
    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Object o, ByteBuf out) throws Exception {
        if(o instanceof MessagePack){
            MessagePack ms = (MessagePack)o;
            out.writeInt(ms.size);
            out.writeByte(ms.routeType);
            out.writeByte(ms.serializeType);
            out.writeInt(ms.seqId);
            out.writeInt(ms.msgType);
            out.writeBytes(ms.message);
        }
    }
}
