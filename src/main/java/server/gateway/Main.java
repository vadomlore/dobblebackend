package server.gateway;

import java.util.Scanner;

/**
 * Created by zyl on 2017/7/14.
 */
public class Main {

  public static void main(String[] args) {
    startGatewayServer();
  }

  public static void startGatewayServer(){
    GatewayServer gatewayServer = new GatewayServer();
    gatewayServer.run();
    System.out.println("Wait for gateway server to stop");
    Scanner scanner = new Scanner(System.in);
    scanner.next();
    gatewayServer.safeClose();
  }

}
