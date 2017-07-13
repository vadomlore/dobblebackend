package script.scripts;

import script.IScript;

import java.util.HashMap;
import java.util.Map;

/**
 * 创建出的Script新实例
 * Created by zyl on 2017/7/13.
 */
public class ScriptManager {

    public static volatile Map<Integer, IScript> scripts = new HashMap<>();

    public static IScript put(int id, IScript script) {
        System.out.println("put script signature:" + script.getSignature());
        return scripts.put(id, script);
    }


    public static IScript get(Integer scriptId) {

        if (!scripts.containsKey(scriptId)) {
            return null;
        }
        IScript script = scripts.get(scriptId);
        System.out.println("get script signature:" + script.getSignature());
        return script;

    }

    public static IScript remove(Integer scriptId) {
        return scripts.remove(scriptId);
    }

    public static boolean contains(int id) {
        return scripts.containsKey(id);
    }
}
