package server;

/**可以查询GameServer和GatewayServer的激活状态
 * Created by zyl on 2017/7/14.
 */
public class QueryServerStatus {
    public static ServerStatus getServerStatus(int serverId){
        return ServerStatus.Active; //todo query status from redis
    }

    public static ServerStatus getServerStatus(String serverName){
        return ServerStatus.Active; //todo query status from redis
    }

    public static ServerStatus getServerStatus(String IpAddress, int port){
        return ServerStatus.Active; //todo query status from redis
    }
}
