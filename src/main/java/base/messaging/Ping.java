package base.messaging;

import io.netty.buffer.Unpooled;


/**
 * Created by zyl on 2017/7/14.
 */
public class Ping {

  private MessagePack pack = new MessagePack();

  public Ping(MessageRouteType routerType){
    pack.size = 14;
    pack.serializeType = SerializeType.Binary.value();
    pack.routeType = routerType.value();
    pack.message = new byte[4];
    long currentTime = System.currentTimeMillis();
    Unpooled.buffer(4).writeLong(currentTime).getBytes(0, pack.message);
  }

  private Ping(){}

  public MessagePack getByteBuf(){
    return pack;
  }

}
