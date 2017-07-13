package server.gameserver;

import base.utility.encrypt.MD5Util;
import script.IScript;
import script.compiler.JavaStringCompiler;
import script.compiler.MemoryClassLoader;
import script.scripts.ScriptManager;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


/**
 * Created by zyl on 2017/7/13.
 */
public class Main {
    public static void main(String[] args) throws IOException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        //DefaultLifeCycleScriptContainer container = new DefaultLifeCycleScriptContainer();
        ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();

        System.out.println( System.getProperty("user.dir"));
        String directory = System.getProperty("user.dir");
        String scriptPath = directory + "/src/main/java/script/callablescript";

        JavaStringCompiler  compiler = new JavaStringCompiler();

        ClassLoader parentClassLoader = MemoryClassLoader.class.getClassLoader();

        ReloadScript(scriptPath, compiler, parentClassLoader);

        new Thread(()-> {
            service.scheduleAtFixedRate(()->{
                IScript script = ScriptManager.get(1001);
                script.execute();
            },0,2, TimeUnit.SECONDS);
        }).start();

        Scanner scanner = new Scanner(System.in);
        while(true)
        {
            String s = scanner.next();
            if(s.equals("n")){
                System.out.println("start reload");
                ReloadScript(scriptPath, compiler, parentClassLoader);
            }
        }
    }


    public static void ReloadScript(String scriptPath, JavaStringCompiler compiler, ClassLoader parentClassLoader) throws IOException, NoSuchMethodException, InvocationTargetException {
        File file=new File(scriptPath);
        File[] tempList = file.listFiles();
        System.out.println("该目录下对象个数："+tempList.length);
        for (int i = 0; i < tempList.length; i++) {
            if (tempList[i].isFile()) {
               // System.out.println("文     件：" + tempList[i].getName());
                String src = new String(Files.readAllBytes(Paths.get(tempList[i].getAbsolutePath())));
                Map<String, byte[]> results = compiler.compile(tempList[i].getName(), src);
                for (Map.Entry<String, byte[]> stringEntry : results.entrySet()) {
                    Class<?> clazz = null;
                    try {
                        clazz = new MemoryClassLoader(results, MemoryClassLoader.class.getClassLoader()).findClass(stringEntry.getKey());
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
                        if (ScriptManager.contains(script.getId())) {
                            IScript oldScript = ScriptManager.get(script.getId());
                            if (!oldScript.getSignature().equals(signature)) {
                                oldScript.destroy();
                                script.init();
                                ScriptManager.put(script.getId(), script);
                            } else {
                                System.out.println("Signature equal.");
                            }
                        } else {
                            ScriptManager.put(script.getId(), script);
                        }
                    }
                }
                System.out.println("b");
            }
            if (tempList[i].isDirectory()) {
                ReloadScript(tempList[i].getAbsolutePath(), compiler, parentClassLoader);
               // System.out.println("文件夹：" + tempList[i]);
            }
        }
    }

}
