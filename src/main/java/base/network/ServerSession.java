package base.network;


import io.netty.channel.Channel;
import server.ServerStatus;

/**
 *
 * Created by Administrator on 2017/7/18.
 */
public class ServerSession extends Session {

    private ServerStatus serverStatus;

    public ServerSession(Long  sessionId, String name, Channel channel){
        super(sessionId, name, channel);
    }

    public ServerStatus getServerStatus(){
        if(this.channel == null){
            return ServerStatus.Inavtive;
        }
        if(this.channel.isActive()){
            return ServerStatus.Active;
        }
        if(!this.channel.isOpen() || !this.channel.isActive()){
            return ServerStatus.Inavtive;
        }
        return ServerStatus.Inavtive;
    }

}
