package base.messaging;

import java.util.Arrays;

/**
 * Created by zyl on 2017/7/14.
 */
public class MessagePack {
  public int size; //消息长度
  public byte routeType; //消息发送目的地  1:c->gate, 2:gate->c 3:c->gs 4: gs->c 5: gate->gs 6.gs->gate ...more
  public byte serializeType; //1 binary, 2 protobuf, 3 json;
  public int seqId; //发送消息的自增序列号，Tcp中用于验证服务器和客户端收发数据的一致性
  public int msgType; //消息类型的具体类型，用于指明消息类型与可能结构的对应关系
  public byte[] message; //具体的消息

  @Override
  public String toString() {
    return "MessagePack{" +
            "size=" + size +
            ", routeType=" + MessageRouteType.type(routeType) +
            ", serializeType=" + SerializeType.type(serializeType) +
            ", seqId=" + seqId +
            ", msgType=" + msgType +
            ", message=" + Arrays.toString(message) +
            '}';
  }
}
