package base.messaging;

//1:c->gate, 2:gate->c 3:c->gs 4: gs->c 5: gate->gs 6.gs->gate ...more
public enum MessageRouteType {

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

}
