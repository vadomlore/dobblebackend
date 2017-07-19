package base.messaging.protobuf;

import base.messaging.MessagePack;
import base.messaging.ProtocolId;
import com.google.protobuf.GeneratedMessage;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

/**
 * Created by zyl on 2017/7/19.
 */
public class ProtoBufferMessagePack extends MessagePack {

    Object object;

    @Override
    public byte getProtocolId() {
        return ProtocolId.Protobuf.value();
    }

    int getBaseSize(){
        return 13;
    }

    public int totalSize(){
        return getBaseSize() + ((GeneratedMessage)object).getSerializedSize();
    }

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }

    public int getMessageType() {
        return ProtoBufferParserFactory.getId(this.getClass());
    }

    @Override
    public ByteBuf wrapMessage() {
        byte[] bytes = ((GeneratedMessage)object).toByteArray();
        return Unpooled.wrappedBuffer(bytes);
    }
}
