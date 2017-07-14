package base.network;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelId;

/**
 *
 * 服务端的服务器启动过程都
 * Created by zyl on 2017/7/14.
 */
public class ServerInfo {

  public ChannelId serverName; //服务器名字

  public ChannelHandlerContext context;

  public Channel channel;
}
