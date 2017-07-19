package base.network;


import io.netty.channel.Channel;
import server.ServerStatus;

/**
 *
 * Created by Administrator on 2017/7/18.
 */
public class ServerAbstractSession extends Session {

    private ServerStatus serverStatus;

    public ServerAbstractSession(Long  sessionId, String name, Channel channel){
        this.sessionId = sessionId;
        this.name = name;
        this.channel = channel;
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
