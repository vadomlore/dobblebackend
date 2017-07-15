package consts;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

/**
 * Created by zyl on 2017/7/14.
 */
public class ServerSettings {

  private static ServerSettings settings;

  public final static String scriptDirectory = System.getProperty("user.dir") + "/src/main/java/script/callablescript";

  public String getProperty(String key){
    return _prop.getProperty(key);
  }

  private ServerSettings(){
    readProperies(SERVER_SETTINGS_FILE_PATH);
  }

  private enum Singleton {

    INSTANCE;

    Singleton() {
      settings = new ServerSettings();
    }

    ServerSettings getInstance() {
      return settings;
    }
  }

  public static ServerSettings getInstance(){

    return Singleton.INSTANCE.getInstance();
  }

  private Properties _prop = new Properties();

  public static final String SERVER_SETTINGS_FILE_PATH = "serversettings.properties";

  private void readProperies(String fileName) {
    try {
      InputStream in = ServerSettings.class.getResourceAsStream("/"+ SERVER_SETTINGS_FILE_PATH);
      BufferedReader bf = new BufferedReader(new InputStreamReader(in));
      _prop.load(bf);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}

