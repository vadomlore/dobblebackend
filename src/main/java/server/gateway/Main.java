package server.gateway;

import consts.ServerSettings;
import org.apache.logging.log4j.core.jmx.Server;

/**
 * Created by zyl on 2017/7/14.
 */
public class Main {

  public static void main(String[] args) {

  }

  public static void startGatewayServer(){
    ServerSettings.getProperty(ServerSettings.SERVER_SETTINGS_FILE_PATH);

    GatewayServer gatewayServer = new GatewayServer();

  }

}
