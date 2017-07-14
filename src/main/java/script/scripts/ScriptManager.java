package script.scripts;

import base.utility.encrypt.MD5Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import script.IScript;
import script.compiler.JavaStringCompiler;
import script.compiler.MemoryClassLoader;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

/**
 * 创建出的Script新实例
 * Created by zyl on 2017/7/13.
 */
public class ScriptManager {

  private static Logger logger = LoggerFactory.getLogger(ScriptManager.class);
  private static ScriptManager scriptManager;

  private enum Singleton {
    INSTANCE;

    Singleton() {
      scriptManager = new ScriptManager();
    }

    ScriptManager getInstance() {
      return scriptManager;
    }
  }

  public static ScriptManager getInstance() {
    return Singleton.INSTANCE.getInstance();
  }

  public volatile Map<Integer, IScript> scripts = new HashMap<>();

  public IScript put(int id, IScript script) {
    System.out.println("put script signature:" + script.getSignature());
    return scripts.put(id, script);
  }


  public IScript get(Integer scriptId) {

    if (!scripts.containsKey(scriptId)) {
      return null;
    }
    IScript script = scripts.get(scriptId);
    System.out.println("get script signature:" + script.getSignature());
    return script;

  }

  public void reloadScript(String scriptPath, JavaStringCompiler compiler) {
    File file = new File(scriptPath);
    File[] tempList = file.listFiles();
    logger.debug("该目录下对象个数：{}", tempList.length);
    try{
      for (int i = 0; i < tempList.length; i++) {
        if (tempList[i].isFile()) {
          String src = new String(Files.readAllBytes(Paths.get(tempList[i].getAbsolutePath())));
          Map<String, byte[]> results = compiler.compile(tempList[i].getName(), src);
          for (Map.Entry<String, byte[]> stringEntry : results.entrySet()) {
            Class<?> clazz = null;
            try {
              clazz = new MemoryClassLoader(results, MemoryClassLoader.class.getClassLoader())
                  .findClass(stringEntry.getKey());
            } catch (ClassNotFoundException e) {
              e.printStackTrace();
            }
            IScript script = null;
            try {
              script = (IScript) clazz.newInstance();
              script.execute();
            } catch (InstantiationException e) {
              e.printStackTrace();
            } catch (IllegalAccessException e) {
              e.printStackTrace();
            }
            String signature = MD5Util.getMD5(src);
            script.setSignature(signature);
            if (script != null) {
              script.getId();
              if (contains(script.getId())) {
                IScript oldScript = get(script.getId());
                if (!oldScript.getSignature().equals(signature)) {
                  oldScript.destroy();
                  script.init();
                  put(script.getId(), script);
                } else {
                  logger.debug("script {} signature equal:" + script.getClass().getName());
                }
              } else {
                put(script.getId(), script);
              }
            }
          }
        }
        if (tempList[i].isDirectory()) {
          reloadScript(tempList[i].getAbsolutePath(), compiler);
        }
      }
    }catch (IOException e){
      logger.debug("File not found {}", e.toString());
    }
  }


  public IScript remove(Integer scriptId) {
    return scripts.remove(scriptId);
  }

  public boolean contains(int id) {
    return scripts.containsKey(id);
  }
}
