package script;


/**
 * reference @link  http://blog.csdn.net/chenjie19891104/article/details/42807959
 * Created by zyl on 2017/7/13.
 */
public interface IScript {

    void setParameter(Object[] parameters);

    void init();

    void execute();

    void destroy();

    String getSignature();

    void setSignature(String signature);

    Integer getId();
}
