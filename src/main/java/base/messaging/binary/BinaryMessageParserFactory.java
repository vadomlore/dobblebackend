package base.messaging.binary;

import base.messaging.MessagePack;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import com.google.common.collect.*;

import java.security.InvalidParameterException;

/**
 * Created by Administrator on 2017/7/18.
 */
public class BinaryMessageParserFactory {

    public static BiMap<Integer, Class> messageIdTypeMap = HashBiMap.create();

    static {
        messageIdTypeMap.put(0, PingMsg.class);
        messageIdTypeMap.put(1, GameServerInitMsg.class);
    }

    public static Class getClass(Integer typeId) {
        return messageIdTypeMap.get(typeId);
    }

    public static int getId(Object clazz) {
        return messageIdTypeMap.inverse().get(clazz);
    }

    private static BinaryMessagePack create(int id) {
        if (!messageIdTypeMap.containsKey(id)) {
            throw new InvalidParameterException(String.format("invalid MessagePack id {0}", id));
        }
        try {
            return (BinaryMessagePack) messageIdTypeMap.get(id).newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static BinaryMessagePack parse(MessagePack messagePack) {
        ByteBuf buf = Unpooled.wrappedBuffer(messagePack.getMessage());
        int messageId = buf.readInt(); //读取二进制消息的消息类型。 对应BinaryMessageParserFactory中的消息类型
        BinaryMessagePack entityPack = BinaryMessageParserFactory.create(messageId);
        entityPack.copy(messagePack);
        int remain = buf.readableBytes();
        byte[] data = new byte[remain];
        buf.readBytes(data);
        entityPack.parseMessage(data);
        return entityPack;
    }

}
