//package script;
//
//import com.google.common.collect.LinkedListMultimap;
//import com.google.common.collect.Multimap;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import script.scripts.LifeCycleScript;
//
//import java.util.Collection;
//
///**
// * 此实例中的Script包含所有AfterEffects的脚本(所有LifeCycle的脚本都需要包含在其中,其中包括主动销毁或者由于热更新替换销毁的脚本
// * 执行替换时，会触发destroy操作),
// * Created by zyl on 2017/7/13.
// */
//public class DefaultLifeCycleScriptContainer implements IScriptContainer {
//
//    public static Logger logger = LoggerFactory.getLogger(DefaultLifeCycleScriptContainer.class);
//
//    private Multimap<ScriptEnum, LifeCycleScript> scripts = LinkedListMultimap.create();
//
//
//    public void register(ScriptEnum type, IScript script) {
//        if(scripts.containsKey(type)){
//            unregister(type);
//        }
//        if(script instanceof LifeCycleScript){
//            scripts.put(type, (LifeCycleScript) script);
//        }
//        else{
//            logger.warn("not cycleScript registerd.");
//        }
//    }
//
//
//    public void unregister(ScriptEnum type) {
//        Collection<LifeCycleScript> _scripts = scripts.get(type);
//        if(_scripts != null){
//            _scripts.forEach(s->
//            s.destroy());
//        }
//        _scripts.remove(type);
//    }
//
//    public static void main(String[] args) {
//
//    }
//}
