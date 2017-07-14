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

  public static Properties _prop = new Properties();

  public static final String SERVER_SETTINGS_FILE_PATH = "serversetttings.properties";

  public static void readProperies(String fileName) {
    try {
      InputStream in = ServerSettings.class.getResourceAsStream("/" + fileName);
      BufferedReader bf = new BufferedReader(new InputStreamReader(in));
      _prop.load(bf);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static final String scriptDirectory = System.getProperty("user.dir") + "/src/main/java/script/callablescript";

  public static String getProperty(String key){
    return _prop.getProperty(key);
  }


}

