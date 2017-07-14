package base.messaging;

/**
 * Created by zyl on 2017/7/14.
 */
public enum  SerializeType {
  Binary(0),
  Protobuf(1),
  Json(2);

  int no;
  SerializeType(int no){
    this.no = no;
  }

  public byte value(){
    return (byte)this.no;
  }
}

