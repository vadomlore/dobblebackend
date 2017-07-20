package base.messaging.protobuf;

import base.messaging.MessagePack;
import com.proto.gamename.Game;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import com.google.common.collect.*;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.InvalidParameterException;
import java.util.HashMap;
import java.util.Map;
import com.google.protobuf.GeneratedMessage;
/**
 * Created by Administrator on 2017/7/18.
 */
public class ProtoBufferParserFactory {

    private final static Map<Integer, Method> parseMap = new HashMap<>();
    private final static BiMap<Integer, Class<? extends  GeneratedMessage>> messageIdTypeMap = HashBiMap.create();

    static
    {
        initializePbMapper(0, Game.Query.class);
        initializePbMapper(1, Game.Answer.class);
        initializePbMapper(2, Game.Empty.class);
        initializePbMapper(3, Game.ServerNode.class);
    }

    public static Class getClass(Integer typeId) {
        return messageIdTypeMap.get(typeId);
    }

    public static int getId(Object clazz) {
        return messageIdTypeMap.inverse().get(clazz);
    }

    private static Class create(int id) {
        if (!messageIdTypeMap.containsKey(id)) {
            throw new InvalidParameterException(String.format("invalid MessagePack id {0}", id));
        }
        return messageIdTypeMap.get(id);

    }

    public static ProtoBufferMessagePack parse(MessagePack messagePack) {
        ByteBuf buf = Unpooled.wrappedBuffer(messagePack.getMessage());
        int id = buf.readInt(); //读取二进制消息的消息类型。 ProtoBufferMessagePack
        Class clazz = messageIdTypeMap.get(id);
        Method method = parseMap.get(id);
        if(clazz == null || method == null) {
            return null;
        }

        ProtoBufferMessagePack entityPack = new ProtoBufferMessagePack();
        entityPack.copy(messagePack);
        byte[] data = new byte[buf.readableBytes()];
        buf.readBytes(data);
        try {
            Object m = method.invoke(clazz, new Object[] { data });
            entityPack.setMessageObject(m);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return entityPack;
    }

    private static void initializePbMapper(int cmd, Class<? extends  GeneratedMessage> clazz){
        messageIdTypeMap.put(cmd, clazz);
        try {
            Method m = clazz.getMethod("parseFrom", new Class[] { byte[].class });
            parseMap.put(cmd, m);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }
}
