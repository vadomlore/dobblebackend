package base.network;

import io.netty.channel.Channel;

/**
 * Created by zyl on 2017/7/14.
 */
public class InitializeRequest {
  private  Channel channel;

  public Channel getChannel() {
    return channel;
  }

  public InitializeRequest(Channel channel){
    this.channel = channel;

  }
}
