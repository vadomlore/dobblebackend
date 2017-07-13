package script.compiler.on.the.fly;
/**
 * Sample class as JavaBean.
 *
 * @author michael
 */
public interface BeanProxy {

    void setDirty(boolean dirty);

    boolean isDirty();

}