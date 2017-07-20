package base.network;

import com.proto.gamename.Game;
import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import server.ServerStatus;

/**
 * Created by zyl on 2017/7/20.
 */

public class GameServerSession extends Session {

    private static final Logger logger = LoggerFactory.getLogger(GameServerSession.class);

    private ServerStatus status;
    private Game.ServerNode node;

    public GameServerSession(Long sessionId, String name, Channel channel) {
        super(sessionId, name, channel);
        status = ServerStatus.Inavtive;
    }

    public ServerStatus getStatus() {
        return status;
    }

    public void setStatus(ServerStatus status) {
        this.status = status;
    }

    public Game.ServerNode getNode() {
        return node;
    }

    public void setNode(Game.ServerNode node) {
        this.node = node;
    }

    @Override
    protected void onRemove(){
        status = ServerStatus.Inavtive;
        logger.info("{} removed.", this);
    }

    @Override
    public String toString() {
        return "GameServerSession{" +
                "status=" + status +
                ", node=" + node +
                '}';
    }
}
