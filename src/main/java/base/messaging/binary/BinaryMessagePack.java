package base.messaging.binary;

import base.messaging.MessagePack;
import base.messaging.ProtocolId;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

/**
 * Created by zyl on 2017/7/19.
 */
public  class BinaryMessagePack extends MessagePack {

    private static final int MessageTypeIdentiferLength = 4;

    public BinaryMessagePack(){

    }

    @Override
    public int getBaseSize(){
        Object clazz = BinaryMessagePack.class;

        return super.getBaseSize() + MessageTypeIdentiferLength;
    }

    @Override
    public byte getProtocolId() {
        return ProtocolId.Binary.value();
    }

    public int getMessageType() {
        return BinaryMessageParserFactory.getId(this.getClass());
    }

    public int totalSize(){
     throw new NotImplementedException();
    }

}
