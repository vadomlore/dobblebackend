package base.messaging;

import com.google.common.primitives.Longs;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import sun.plugin2.message.Message;


/**
 * Created by zyl on 2017/7/14.
 */
public class Ping {

  public static Integer id = 0;

  private MessagePack pack = new MessagePack();

  private long timestamp;

  public Ping(MessagePack pack){
    this.pack = pack;
  }

  public Ping(MessageRouteType routerType){
    pack.size = 22;
    pack.routeType = routerType.value();
    pack.serializeType = SerializeType.Binary.value();
    pack.seqId = id++;
    pack.msgType = 0; //0 代表心跳包
    byte[] data = new byte[8];
    long currentTime = System.currentTimeMillis();
    ByteBuf buf = Unpooled.buffer(8).writeLong(currentTime);
    buf.readBytes(data);
    pack.message = data;
  }

  public static Ping fromMessagePack(MessagePack pack){
    if(pack.msgType != 0) return null;
    Ping ping = new Ping(pack);
    ping.timestamp = Longs.fromByteArray(pack.message);
    return ping;
  }

  @Override
  public String toString() {
    return "Ping{" +
            "timestamp=" + timestamp +
            '}';
  }

  private Ping(){}

  public MessagePack getPack(){
    return pack;
  }



}
