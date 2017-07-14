package base.network;

import io.netty.channel.Channel;
import java.net.SocketAddress;

/**
 * Created by zyl on 2017/7/14.
 */
public class PeerInfo {

  private Channel channel;

  public PeerInfo(Channel channel){
    this.channel = channel;
  }

  public SocketAddress getSockInfo(){
    return this.channel.localAddress();
  }

  public SocketAddress getPeerInfo(){
    return this.channel.remoteAddress();
  }
}
