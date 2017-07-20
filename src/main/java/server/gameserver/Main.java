package server.gameserver;

import consts.ServerSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import script.compiler.JavaStringCompiler;
import script.scripts.ScriptManager;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.Inet4Address;


/**
 * Created by zyl on 2017/7/13.
 */
public class Main {

    private static Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args)
            throws IOException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {

        reloadScripts();

        String ip = null;
        int port = 0;
        String name = null;
        GameServer gameServer = null;
        if (args.length == 3) {
            try {
                name = args[0];
                ip = args[1];
                port = Integer.parseInt(args[2]);
                gameServer = new GameServer(name, ip, port);
            } catch (NumberFormatException e) {
                logger.error("parseException");
            }
        }else if(args.length == 0){
            gameServer = new GameServer();
        }
        gameServer.run();


//    Scanner scanner = new Scanner(System.in);
//    while (true) {
//      String s = scanner.next();
//      if (s.equals("n")) {
//        System.out.println("run reload");
//        ScriptManager.getInstance().reloadScript(scriptDirectory, compiler);
//      }
//    }

    }

    static void reloadScripts() {
//    ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();

        JavaStringCompiler compiler = new JavaStringCompiler();
        ScriptManager.getInstance().reloadScript(ServerSettings.scriptDirectory, compiler);


//    new Thread(() -> {
//      service.scheduleAtFixedRate(() -> {
//        IScript script = ScriptManager.getInstance().get(1001);
//        script.execute();
//      }, 0, 2, TimeUnit.SECONDS);
//    }).run();
    }

}

