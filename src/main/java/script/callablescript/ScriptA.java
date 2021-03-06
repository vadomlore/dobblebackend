package script.callablescript;

import java.time.LocalDate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import script.IScript;

/**
 * Created by zyl on 2017/7/13.
 */
public class ScriptA implements IScript {


  public static final Logger logger = LoggerFactory.getLogger(ScriptA.class);

  private String signature;

  public int m = 45454;

  @Override
  public void setParameter(Object[] parameters) {

  }

  @Override
  public void init() {

  }

  @Override
  public void execute() {
    logger.debug(LocalDate.now() + this.getClass().getName() + " MMMMM");
//    System.out.println(LocalDate.now() + this.getClass().getName() + " MMMMM");
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
