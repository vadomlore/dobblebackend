package server.gameserver;

import static consts.ServerSettings.scriptDirectory;

import script.IScript;
import script.compiler.JavaStringCompiler;
import script.scripts.ScriptManager;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


/**
 * Created by zyl on 2017/7/13.
 */
public class Main {

  public static void main(String[] args)
      throws IOException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {

      reloadScripts();



//    Scanner scanner = new Scanner(System.in);
//    while (true) {
//      String s = scanner.next();
//      if (s.equals("n")) {
//        System.out.println("start reload");
//        ScriptManager.getInstance().reloadScript(scriptDirectory, compiler);
//      }
//    }

  }

  static void reloadScripts () {
//    ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();

    JavaStringCompiler compiler = new JavaStringCompiler();
    ScriptManager.getInstance().reloadScript(scriptDirectory, compiler);
    new GameServer().run();

//    new Thread(() -> {
//      service.scheduleAtFixedRate(() -> {
//        IScript script = ScriptManager.getInstance().get(1001);
//        script.execute();
//      }, 0, 2, TimeUnit.SECONDS);
//    }).start();
  }

}

