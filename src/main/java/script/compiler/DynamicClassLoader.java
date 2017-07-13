package script.compiler;



import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * DynamicClassLoader 动态类加载器
 *
 * @author Lilin
 * @date 2016/5/23
 */
public class DynamicClassLoader extends ClassLoader {

    Map<String, byte[]> classBytes = new HashMap<String, byte[]>();

    public DynamicClassLoader(ClassLoader parent, Map<String, byte[]> classBytes) {
        super(parent);
        this.classBytes.putAll(classBytes);
    }


    @Override
    public Class<?> loadClass(String name) throws ClassNotFoundException {
        // 判断当前加载的类是否是需要动态重新加载的类，
        // 假如是通过重写的findClass在自定义的ClassLoader里面加载，
        // 假如不是就调用父ClassLoader默认加载
        if (name != null && name.equals(name)) {
            return findClass(name);
        }
        return super.loadClass(name, false);
    }

    /**
     * 根据类名查找class
     *
     * @param fullClassPath 类全路径（包）
     * @return
     * @throws ClassNotFoundException
     */
    @Override
    protected Class<?> findClass(String name)
            throws ClassNotFoundException {

        byte[] buf = classBytes.get(name);
        if (buf == null) {
            return super.findClass(name);
        }
        classBytes.remove(name);

        //byte[] raw = readClassBytes(fullClassPath);
        //                         b：读取的class文件的byte数组
        //                         off：从byte数组中读取的索引
        //                         len：从byte数组中读取的长度
        // 注：假如此类中有引入别的class类，循环执行findClass方法
        Class<?> clazz = defineClass(name, buf, 0, buf.length);
        // 连接class
        resolveClass(clazz);
        return clazz;
    }

}