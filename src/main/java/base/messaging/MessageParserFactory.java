package base.messaging;

import io.netty.handler.codec.MessageAggregationException;

import java.security.InvalidParameterException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

/**
 * Created by Administrator on 2017/7/18.
 */
public class MessageParserFactory {

    private static MessagePack create(int id){
        switch (id){
            case 0:
                return new PingMsg();
            case 1:
                return new GameServerInitMsg();
            default:
                throw new InvalidParameterException(String.format("invalid MessagePack id {0}", id));
        }
    }

    public MessagePack parse(int id, MessagePack messagePack){
        MessagePack pack = MessageParserFactory.create(id);
        pack.parseMessage(messagePack);
        return pack;
    }

}
