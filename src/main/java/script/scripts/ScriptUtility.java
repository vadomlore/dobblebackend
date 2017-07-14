package script.scripts;

import script.IScript;

/**
 * Created by zyl on 2017/7/13.
 */
public class ScriptUtility {

  public boolean isNull(IScript script) {
    return script.getId() == 0;
  }

  public void setParameters(IScript script, Object[] params) {
    script.setParameter(params);
  }
}
