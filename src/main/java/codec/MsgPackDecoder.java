package codec;

import base.messaging.MessagePack;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageCodec;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.CodecException;
import io.netty.handler.codec.MessageToByteEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.plugin2.message.Message;

import java.util.List;

/**
 * Created by Administrator on 2017/7/15.
 */
public class MsgPackDecoder extends ByteToMessageDecoder{
    static final Logger logger = LoggerFactory.getLogger(MsgPackDecoder.class);
    static final int MAX_PACKAGE_SIZE = 64 * 1024; //64k;
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf byteBuf, List<Object> list) throws Exception {
        if(byteBuf.readableBytes() < 4){
            return;
        }
        int totalLength =  byteBuf.getInt(0);
        if(totalLength > MAX_PACKAGE_SIZE){
            logger.error("channel:{} packge size {} exceed the limit {}", ctx.channel(), totalLength, MAX_PACKAGE_SIZE);
            ctx.close();
        }
        if(totalLength < 14){ //消息格式限制至少14byte
            logger.error("channel:{} packge size {} invlid", ctx.channel(), totalLength);
            ctx.close();
        }

        if(byteBuf.readableBytes()>= totalLength){
            MessagePack pack = new MessagePack();
            byteBuf.readInt();
            pack.size = totalLength;
            pack.routeType = byteBuf.readByte();
            pack.serializeType = byteBuf.readByte();
            pack.seqId = byteBuf.readInt();
            pack.msgType = byteBuf.readInt();
            byte[] message = new byte[totalLength - 14];
            byteBuf.readBytes(message);
            pack.message = message;
            list.add(pack);
        }

    }
}
