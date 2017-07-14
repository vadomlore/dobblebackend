import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

/**
 * Created by zyl on 2017/7/14.
 */
public class NetworkUtility {
  static AtomicInteger atomicInteger = new AtomicInteger(0);
  public static int fakeGenerateUniqueId(){
    return atomicInteger.incrementAndGet(); //fake, implement with redis
  }


  public static int generateUniqueId(){
    throw new NotImplementedException(); //implement with redis
  }
}
