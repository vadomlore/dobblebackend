package codec;

import base.messaging.binary.BinaryMessageParserFactory;
import base.messaging.MessagePack;
import base.messaging.protobuf.ProtoBufferParserFactory;
import base.messaging.ProtocolId;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
        if(totalLength < 10){ //消息格式限制至少14byte
            logger.error("channel:{} packge size {} invlid", ctx.channel(), totalLength);
            ctx.close();
        }

        MessagePack pack = new MessagePack();
        if(byteBuf.readableBytes()>= totalLength){
            byteBuf.readInt();
            pack.setSize(totalLength);
            pack.setProtocolId(byteBuf.readByte());
            pack.setSessionId(byteBuf.readLong());
            byte[] message = new byte[totalLength - pack.getBaseSize()];
            byteBuf.readBytes(message);
            pack.setMessage(message);
            pack.setReceiveTimestamp(System.currentTimeMillis());
        }

        MessagePack newPack = null;
        switch (ProtocolId.type(pack.getProtocolId())){
            case Binary:
                newPack = BinaryMessageParserFactory.parse(pack);
                break;
            case Protobuf:
                newPack = ProtoBufferParserFactory.parse(pack);
                break;
            default:
                break;
        }
        if(newPack != null){
            list.add(newPack);
        }
        else {
            logger.warn("invalid Msg pack decode");
        }
    }
}
