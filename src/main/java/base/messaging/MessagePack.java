package base.messaging;

import io.netty.buffer.ByteBuf;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.Arrays;

/**
 * Created by zyl on 2017/7/14.
 */
public class MessagePack {

  private int size; //消息最小长度

  //public byte routeType; //消息发送目的地  1:c->gate, 2:gate->c 3:c->gs 4: gs->c 5: gate->gs 6.gs->gate ...more

  private byte protocolId; //1 binary, 2 protobuf, 3 json;

  private long sessionId; //发送者Id 身份确认

  private byte[] message; //具体的消息，不包含MsgId，protobuf名字前缀等等

  private Long receiveTimestamp;

  public MessagePack() {
  }

  public int getBaseSize(){
    return 13;
  }

  public byte[] getMessage() {
    return message;
  }

  public byte getProtocolId() {
    return protocolId;
  }

  public long getSessionId() {
    return sessionId;
  }

  public int getSize() {
    return size;
  }

  /**
   * do not call this method when build up your own message
   * @param size
   */
  public void setSize(int size) {
    this.size = size;
  }

  public void setProtocolId(byte protocolId) {
    this.protocolId = protocolId;
  }

  public void setSessionId(long sessionId) {
    this.sessionId = sessionId;
  }

  public void setMessage(byte[] message) {
    this.message = message;
  }

  public Long getReceiveTimestamp() {
    return receiveTimestamp;
  }

  public void setReceiveTimestamp(Long receiveTimestamp) {
    this.receiveTimestamp = receiveTimestamp;
  }


  public byte[] wrapMessage() {
    throw new NotImplementedException();
  }

  @Override
  public String toString() {
    return "MessagePack{" +
            "size=" + size +
//            ", routeType=" + MessageRouteType.type(routeType) +
            ", protocolId=" + ProtocolId.type(protocolId) +
            ", sessionId=" + sessionId +
            ", message=" + Arrays.toString(message) +
            '}';
  }

  public void copy(MessagePack pack){
    this.size = pack.size;
//    this.routeType = pack.routeType;
    this.protocolId = pack.protocolId;
    this.sessionId = pack.sessionId;
    this.message = pack.message;
  }


  public void parseMessage(byte[] data){
    throw new NotImplementedException();
  }

  public ByteBuf pack(){
    throw new NotImplementedException();
  }
}
