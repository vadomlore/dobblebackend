package server;

/**
 * Created by zyl on 2017/7/14.
 */
public class ServerBean {

  public ServerBean(int serverUniqueId, ServerType serverType, String serverName,
      ServerStatus serverStatus, String ipAddress, int port) {
    this.serverUniqueId = serverUniqueId;
    this.serverType = serverType;
    this.serverName = serverName;
    this.serverStatus = serverStatus;
    IpAddress = ipAddress;
    this.port = port;
  }

  private int serverUniqueId;

  private ServerType serverType;

  private String serverName;

  private ServerStatus serverStatus;

  private String IpAddress;

  private int port;

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    ServerBean that = (ServerBean) o;
    return serverUniqueId == that.serverUniqueId;
  }

  @Override
  public int hashCode() {
    int result = serverUniqueId;
    result = 31 * result + (serverType != null ? serverType.hashCode() : 0);
    result = 31 * result + (serverName != null ? serverName.hashCode() : 0);
    result = 31 * result + (serverStatus != null ? serverStatus.hashCode() : 0);
    result = 31 * result + (IpAddress != null ? IpAddress.hashCode() : 0);
    result = 31 * result + port;
    return result;
  }


  public int getServerUniqueId() {
    return serverUniqueId;
  }

  public void setServerUniqueId(int serverUniqueId) {
    this.serverUniqueId = serverUniqueId;
  }

  public ServerType getServerType() {
    return serverType;
  }

  public void setServerType(ServerType serverType) {
    this.serverType = serverType;
  }

  public String getServerName() {
    return serverName;
  }

  public void setServerName(String serverName) {
    this.serverName = serverName;
  }

  public ServerStatus getServerStatus() {
    return serverStatus;
  }

  public void setServerStatus(ServerStatus serverStatus) {
    this.serverStatus = serverStatus;
  }

  public String getIpAddress() {
    return IpAddress;
  }

  public void setIpAddress(String ipAddress) {
    IpAddress = ipAddress;
  }

  public int getPort() {
    return port;
  }

  public void setPort(int port) {
    this.port = port;
  }


  public void store(){}

  public void reload(){}

  @Override
  public String toString() {
    return "ServerBean{" +
        "serverUniqueId=" + serverUniqueId +
        ", serverType=" + serverType +
        ", serverName='" + serverName + '\'' +
        ", serverStatus=" + serverStatus +
        ", IpAddress='" + IpAddress + '\'' +
        ", port=" + port +
        '}';
  }
}
