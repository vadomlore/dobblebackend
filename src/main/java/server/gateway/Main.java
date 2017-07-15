package server.gateway;

/**
 * Created by zyl on 2017/7/14.
 */
public class Main {

  public static void main(String[] args) {
    startGatewayServer();
  }

  public static void startGatewayServer(){
    GatewayServer gatewayServer = new GatewayServer();
    gatewayServer.start();
  }

}
