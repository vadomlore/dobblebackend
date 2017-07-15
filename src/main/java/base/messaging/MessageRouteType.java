package base.messaging;

import sun.plugin2.message.Message;

//1:c->gate, 2:gate->c 3:c->gs 4: gs->c 5: gate->gs 6.gs->gate ...more
public enum MessageRouteType {

  Unknown(0),
  ClientToGate(1),
  GateToClient(2),
  ClientToGameServer(3),
  GameServerToClient(4),
  GateToGameServer(5),
  GameServerToGate(6);

  int no;
  MessageRouteType(int no){
    this.no = no;
  }

  public byte value(){
    return (byte)this.no;
  }

  public static MessageRouteType type(byte value){
    for(MessageRouteType t : MessageRouteType.values()){
      if(t.value() == value){
        return t;
      }
    }
    return Unknown;
  }
}
