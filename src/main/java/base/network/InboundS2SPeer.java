package base.network;

import base.messaging.MessagePack;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by zyl on 2017/7/14.
 */
public class InboundS2SPeer extends PeerBase {

  public static Logger logger = LoggerFactory.getLogger(ClientPeer.class);

  Channel channel;

  PeerInfo peerInfo;

  @Override
  public ConnectionStatus getConnectionStatus(){
    if(this.channel == null){
      return ConnectionStatus.Disconnect;
    }
    if(!this.channel.isActive()||!this.channel.isOpen()){
      return ConnectionStatus.Disconnect;
    }
    if(this.channel.isActive()){
      return ConnectionStatus.Connected;
    }
    return ConnectionStatus.Disconnect;
  }

  @Override
  void initialize(InitializeRequest request) {
    this.channel = request.getChannel();
    peerInfo = new PeerInfo(request.getChannel());
  }


  @Override
  public void disconnect() {
    if(channel != null){
      channel.close().addListener(
          (ChannelFuture channelFuture)-> {
            if(channelFuture.isSuccess()){
              onDisconnected();
            }
          }
      );
      return;
    }
    logger.warn("channel is null for clientpeer.");
  }


  @Override
  public void sendMessage(MessagePack messagePack) {
    channel.writeAndFlush(messagePack);
  }

  @Override
  public PeerInfo getPeerInfo() {
    return peerInfo;
  }


  @Override
  public boolean connected() {
    return this.channel != null && this.channel.isOpen() && this.channel.isActive();
  }

  @Override
  public void onDisconnected() {
  }
}
