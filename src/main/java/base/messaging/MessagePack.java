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

  private int seqId; //发送消息的自增序列号，用于验证服务器和客户端收发数据一致性

  private byte[] message; //具体的消息，不包含MsgId，protobuf名字前缀等等

  private Long receiveTimestamp;

  public MessagePack() {
  }

  public int baseSize(){
    return 9;
  }

  public byte[] getMessage() {
    return message;
  }

  public byte getProtocolId() {
    return protocolId;
  }

  public int getSeqId() {
    return seqId;
  }

  public void writeSeqId(int seqId) {
    this.seqId = seqId;
  }

  public int getSize() {
    return size;
  }

  public void setSize(int size) {
    this.size = size;
  }

  public void setProtocolId(byte protocolId) {
    this.protocolId = protocolId;
  }

  public void setSeqId(int seqId) {
    this.seqId = seqId;
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


  public ByteBuf wrapMessage() {
    throw new NotImplementedException();
  }

  @Override
  public String toString() {
    return "MessagePack{" +
            "size=" + size +
//            ", routeType=" + MessageRouteType.type(routeType) +
            ", protocolId=" + ProtocolId.type(protocolId) +
            ", seqId=" + seqId +
            ", message=" + Arrays.toString(message) +
            '}';
  }

  public void copy(MessagePack pack){
    this.size = pack.size;
//    this.routeType = pack.routeType;
    this.protocolId = pack.protocolId;
    this.seqId= pack.seqId;
    this.message = pack.message;
  }


  public void parseMessage(byte[] data){
    throw new NotImplementedException();
  }

  public ByteBuf pack(){
    throw new NotImplementedException();
  }
}
