package base.messaging;

import com.google.common.primitives.Longs;
import com.sun.javaws.exceptions.InvalidArgumentException;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.CodecException;
import sun.plugin2.message.Message;

/**
 * Created by zyl on 2017/7/14.
 */
public class PingMsg extends MessagePack{
  public static Integer id = 0;
  private long timestamp;

  public PingMsg(long timestamp){
    this.seqId = id++;
    this.timestamp = timestamp;

  }
//
//      this.size = 22;
//    this.routeType = routerType.value();
//    this.serializeType = SerializeType.Binary.value();
//
//    this.msgType = 0; //0 代表心跳包
//  byte[] data = new byte[8];
//  long currentTime = System.currentTimeMillis();
//  ByteBuf buf = Unpooled.buffer(8).writeLong(currentTime);
//    buf.readBytes(data);
//    this.message = data;




  public void fromMessagePack(MessagePack pack) {
    if(pack.msgType != 0)
      throw new CodecException(String.format("msgType {0} invalid.", pack.msgType));
    this.timestamp = Longs.fromByteArray(pack.message);
  }

  @Override
  public String toString() {
    return "PingMsg{" +
            "timestamp=" + timestamp +
            '}';
  }

  @Override
  public void parseMessage(MessagePack pack) {

  }
}
