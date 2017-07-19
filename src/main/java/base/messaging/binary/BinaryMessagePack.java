package base.messaging.binary;

import base.messaging.MessagePack;
import base.messaging.ProtocolId;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

/**
 * Created by zyl on 2017/7/19.
 */
public  class BinaryMessagePack extends MessagePack {

    public BinaryMessagePack(){

    }

    @Override
    public byte getProtocolId() {
        return ProtocolId.Binary.value();
    }

    public int getMessageType() {
        return BinaryMessageParserFactory.getId(this.getClass());
    }

    int getBaseSize(){
        return 13;
    }

    public int totalSize(){
     throw new NotImplementedException();
    }

}
