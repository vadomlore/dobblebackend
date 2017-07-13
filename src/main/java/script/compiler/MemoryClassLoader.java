package script.compiler;

import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.Map;

/**
 * Load class from byte[] which is compiled in memory.
 *
 * @author michael
 */
public class MemoryClassLoader extends ClassLoader {

    // class name to class bytes:
    Map<String, byte[]> classBytes = new HashMap<String, byte[]>();

    public MemoryClassLoader(Map<String, byte[]> classBytes, ClassLoader parent) {
        super(parent);
        this.classBytes.putAll(classBytes);
    }

    @Override
    public Class<?> findClass(String name) throws ClassNotFoundException {
        byte[] buf = classBytes.get(name);
        if (buf == null) {
            return super.findClass(name);
        }
        classBytes.remove(name);
        Class clazz =  defineClass(name, buf, 0, buf.length);
        //resolveClass(clazz);
        return clazz;
    }

//    /**
//     * Load class from compiled classes.
//     *
//     * @param name
//     *            Full class name.
//     * @param classBytes
//     *            Compiled results as a Map.
//     * @return The Class instance.
//     * @throws ClassNotFoundException
//     *             If class not found.
//     * @throws IOException
//     *             If load error.
//     */
//    public Class<?> loadClass(String name, Map<String, byte[]> classBytes, ClassLoader parentClassLoader) throws ClassNotFoundException, IOException {
//
//        return findClass(name);
//    }
}
