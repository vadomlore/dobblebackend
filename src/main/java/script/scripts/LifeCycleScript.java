//package script.scripts;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import script.IScript;
//
///**
// * Created by zyl on 2017/7/13.
// */
//public abstract class LifeCycleScript implements IScript {
//
//    public static Logger logger = LoggerFactory.getLogger(LifeCycleScript.class);
//
//    boolean enabled = true;
//
//    private String uniqueId;
//
//    protected void init()
//    {
//        this.uniqueId = this.genScriptUniqueId();
//        logger.debug("=>scriptType:[{}]  scriptId:[] initialized", this.getScriptType(), this.getScriptType());
//    }
//
//    public String getUniqueId() {
//        return this.uniqueId;
//    }
//
//
//    @Override
//    public void execute() {
//        if(isEnable()){
//            doExecute();
//        }
//        logger.info("=>scriptType:[{}]  scriptId:[] is disabled", this.getScriptType(), this.getScriptType());
//    }
//
//
//    @Override
//    public void enable() {
//        enabled = true;
//    }
//
//    @Override
//    public void disable() {
//        enabled = false;
//    }
//
//    @Override
//    public boolean isEnable() {
//        return enabled;
//    }
//
//    @Override
//    public boolean isDisable() {
//        return !enabled;
//    }
//
//    @Override
//    public abstract ScriptEnum getScriptType();
//
//    public abstract void doExecute();
//
//    public abstract void destroy();
//}
