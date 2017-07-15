package base.messaging;

/**
 * Created by zyl on 2017/7/14.
 */
public enum  SerializeType {

  Unknown(0),
  Binary(1),
  Protobuf(2),
  Json(3);

  int no;

  SerializeType(int no) {
    this.no = no;
  }

  public byte value() {
    return (byte) this.no;
  }

  public static SerializeType type(byte value) {
    for (SerializeType t : SerializeType.values()) {
      if (t.value() == value) {
        return t;
      }
    }
    return Unknown;
  }
}

