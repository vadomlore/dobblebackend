package base.messaging.binary;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

/**
 * Created by zyl on 2017/7/14.
 */
public class PingMsg extends BinaryMessagePack {

  public static Integer id = 0;

  private long timestamp;

  public PingMsg(){}

  public PingMsg(long timestamp){
    this.writeSeqId(id++);
    this.timestamp = timestamp;
  }

  public long getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(long timestamp) {
    this.timestamp = timestamp;
  }

  @Override
  public String toString() {
    return "PingMsg{" +
            "timestamp=" + timestamp +
            '}';
  }

  /**
   * must implement
   * @param data
   */
  @Override
  public void parseMessage(byte[] data) {
    this.timestamp = Unpooled.wrappedBuffer(data).readLong();
  }

  /**
   * must implement
   * @return
   */
  @Override
  public ByteBuf wrapMessage(){
    ByteBuf buf = Unpooled.buffer(8);
    buf.writeLong(timestamp);
    return buf;
  }

  /**
   * 一般情况不用自己手动实现，此处只是示例
   * @return
   */
  @Override
  public ByteBuf pack(){
    ByteBuf buf = Unpooled.buffer(21);
    buf.writeInt(getBaseSize() + 4);
    buf.writeByte(this.getProtocolId());
    buf.writeInt(this.getSeqId());
    buf.writeByte(BinaryMessageParserFactory.getId(PingMsg.class));
    buf.writeLong(timestamp);
    return buf;
  }

  @Override
  public int totalSize(){
    return getBaseSize() + 8;
  }
}
