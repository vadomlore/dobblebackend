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

    Object messageObject; //具体的protobuf message

    public ProtoBufferMessagePack(){}

    private static final int MessageTypeIdentiferLength = 4;

    @Override
    public int getBaseSize(){
        return super.getBaseSize() + MessageTypeIdentiferLength;
    }

    @Override
    public byte getProtocolId() {
        return ProtocolId.Protobuf.value();
    }

    public int totalSize(){
        return getBaseSize() + ((GeneratedMessage) messageObject).getSerializedSize();
    }

    public Object getMessageObject() {
        return messageObject;
    }

    public void setMessageObject(Object messageObject) {
        this.messageObject = messageObject;
    }

    public int getMessageType() {
        return ProtoBufferParserFactory.getId(this.messageObject.getClass());
    }

    @Override
    public byte[] wrapMessage() {
        byte[] bytes = ((GeneratedMessage) messageObject).toByteArray();
        return bytes;
    }
}
