package codec;

import base.messaging.protobuf.ProtoBufferMessagePack;
import base.messaging.binary.BinaryMessagePack;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Administrator on 2017/7/15.
 */
public class MsgPackEncoder extends MessageToByteEncoder {
    static final Logger logger = LoggerFactory.getLogger(MsgPackEncoder.class);

    @Override
    protected void encode(ChannelHandlerContext ctx, Object o, ByteBuf out) throws Exception {
        if (o instanceof BinaryMessagePack) {
            BinaryMessagePack ms = (BinaryMessagePack) o;
            out.writeInt(ms.totalSize());
            out.writeByte(ms.getProtocolId());
            out.writeInt(ms.getSeqId());
            out.writeInt(ms.getMessageType());
            out.writeBytes(ms.wrapMessage());
        } else if (o instanceof ProtoBufferMessagePack) {
            ProtoBufferMessagePack ms = (ProtoBufferMessagePack) o;
            out.writeInt(ms.totalSize());
            out.writeByte(ms.getProtocolId());
            out.writeInt(ms.getSeqId());
            out.writeInt(ms.getMessageType());
            out.writeBytes(ms.wrapMessage());
        } else if (o instanceof ByteBuf) {
            out.writeBytes((ByteBuf) o);
        } else {
            logger.error("{} {} encode invalid message", MsgPackEncoder.class, o);
            ctx.channel().close();
        }
    }
}
