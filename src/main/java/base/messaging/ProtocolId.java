package base.messaging;

/**
 * Created by zyl on 2017/7/14.
 */
public enum ProtocolId {

  Unknown(0),
  Binary(1),
  Protobuf(2),
  Json(3);

  int no;

  ProtocolId(int no) {
    this.no = no;
  }

  public byte value() {
    return (byte) this.no;
  }

  public static ProtocolId type(byte value) {
    for (ProtocolId t : ProtocolId.values()) {
      if (t.value() == value) {
        return t;
      }
    }
    return Unknown;
  }
}

