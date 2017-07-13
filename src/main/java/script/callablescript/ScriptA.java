package script.callablescript;

import script.IScript;

/**
 * Created by zyl on 2017/7/13.
 */
public class ScriptA implements IScript {
    String a = "8777777777777777777777777777";
    private String signature;


    public int m = 999;
    @Override
    public void setParameter(Object[] parameters) {

    }

    @Override
    public void init() {

    }

    @Override
    public void execute() {
        System.out.println("script.execute()" + this.getClass().getName() + " XXXXXXXXXXXXXXXXX");
    }

    @Override
    public void destroy() {

    }

    @Override
    public String getSignature() {
        return signature;
    }

    @Override
    public void setSignature(String signature) {
        this.signature = signature;

    }

    @Override
    public Integer getId() {
        return 1001;
    }


}
