package base.messaging.binary;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;

/**
 * Created by zyl on 2017/7/14.
 */
public class PingMsg extends BinaryMessagePack {

  private long timestamp;

  public PingMsg(){}

  public PingMsg(long timestamp){
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
  public byte[] wrapMessage(){
    ByteBuf buf = Unpooled.buffer(8);
    buf.writeLong(timestamp);
    return ByteBufUtil.getBytes(buf, 0, 8);
  }

  /**
   * 一般情况不用自己手动实现，此处只是示例
   * @return
   */
  @Override
  public ByteBuf pack(){
    ByteBuf buf = Unpooled.buffer(24);
    buf.writeInt(getBaseSize() + 4);
    buf.writeByte(this.getProtocolId());
    buf.writeLong(this.getSessionId());
    buf.writeByte(BinaryMessageParserFactory.getId(PingMsg.class));
    buf.writeLong(timestamp);
    return buf;
  }

  @Override
  public int totalSize(){
    return getBaseSize() + 8;
  }
}
