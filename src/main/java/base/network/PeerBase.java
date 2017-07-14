package base.network;

import base.messaging.MessagePack;

/**
 * Created by zyl on 2017/7/14.
 */
public abstract class PeerBase {

  abstract void initialize(InitializeRequest request);

  public abstract void disconnect();

  public void sendMessage(MessagePack messagPack){}

  public abstract PeerInfo getPeerInfo();

  public void onReceive(MessagePack messagePack){}

  public abstract ConnectionStatus getConnectionStatus();

  public abstract boolean connected();

  public abstract void onDisconnected();

}
