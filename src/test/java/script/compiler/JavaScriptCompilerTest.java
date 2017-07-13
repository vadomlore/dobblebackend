//package script.compiler;
//
//
//import script.compiler.on.the.fly.BeanProxy;
//import script.compiler.on.the.fly.User;
//import org.junit.Before;
//import org.junit.Test;
//
//import java.lang.reflect.Method;
//import java.util.Map;
//
//import static junit.framework.TestCase.assertEquals;
//import static junit.framework.TestCase.assertNotNull;
//import static junit.framework.TestCase.assertTrue;
//
///**
// * Created by zyl on 2017/7/13.
// */
//public class JavaScriptCompilerTest {
//    JavaStringCompiler compiler;
//
//    @Before
//    public void setUp()throws Exception{
//        compiler = new JavaStringCompiler();
//    }
//
//    static final String SINGLE_JAVA = "/* a single java class to one file */  "
//            + "package extension.script.compiler.on.the.fly;                                            "
//            + "import extension.script.compiler.on.the.fly.*;                            "
//            + "public class UserProxy extends User implements BeanProxy {     "
//            + "    boolean _dirty = false;                                    "
//            + "    public void setId(String id) {                             "
//            + "        super.setId(id);                                       "
//            + "        setDirty(true);                                        "
//            + "    }                                                          "
//            + "    public void setName(String name) {                         "
//            + "        super.setName(name);                                   "
//            + "        setDirty(true);                                        "
//            + "    }                                                          "
//            + "    public void setCreated(long created) {                     "
//            + "        super.setCreated(created);                             "
//            + "        setDirty(true);                                        "
//            + "    }                                                          "
//            + "    public void setDirty(boolean dirty) {                      "
//            + "        this._dirty = dirty;                                   "
//            + "    }                                                          "
//            + "    public boolean isDirty() {                                 "
//            + "        return this._dirty;                                    "
//            + "    }                                                          "
//            + "}";
//
//
//    static final String MULTIPLE_JAVA = "/* a single class to many files */   "
//            + "package extension.script.compiler.on.the.fly;                                            "
//            + "import java.util.*;                                            "
//            + "public class Multiple {                                        "
//            + "    List<Bird> list = new ArrayList<Bird>();                   "
//            + "    public void add(String name) {                             "
//            + "        Bird bird = new Bird();                                "
//            + "        bird.name = name;                                      "
//            + "        this.list.add(bird);                                   "
//            + "    }                                                          "
//            + "    public Bird getFirstBird() {                               "
//            + "        return this.list.get(0);                               "
//            + "    }                                                          "
//            + "    public static class StaticBird {                           "
//            + "        public int weight = 100;                               "
//            + "    }                                                          "
//            + "    class NestedBird {                                         "
//            + "        NestedBird() {                                         "
//            + "            System.out.println(list.size() + \" birds...\");   "
//            + "        }                                                      "
//            + "    }                                                          "
//            + "}                                                              "
//            + "/* package level */                                            "
//            + "class Bird {                                                   "
//            + "    String name = null;                                        "
//            + "}                                                              ";
//
//    @Test
//    public void testCompileMultipleClasses() throws Exception {
//        Map<String, byte[]> results = compiler.compile("Multiple.java", MULTIPLE_JAVA);
//        assertEquals(4, results.size());
//        assertTrue(results.containsKey("extension.script.compiler.on.the.fly.Multiple"));
//        assertTrue(results.containsKey("extension.script.compiler.on.the.fly.Multiple$StaticBird"));
//        assertTrue(results.containsKey("extension.script.compiler.on.the.fly.Multiple$NestedBird"));
//        assertTrue(results.containsKey("extension.script.compiler.on.the.fly.Bird"));
//        Class<?> clzMul = compiler.loadClass("extension.script.compiler.on.the.fly.Multiple", results);
//        // try instance:
//        Object obj = clzMul.newInstance();
//        assertNotNull(obj);
//    }
//
//    @Test
//    public void testComileStringClass() throws Exception{
//        Map<String, byte[]> results = compiler.compile("UserProxy.java", SINGLE_JAVA);
//        assertEquals(1, results.size());
//        assertTrue(results.containsKey("extension.script.compiler.on.the.fly.UserProxy"));
//        Class<?> clazz = compiler.loadClass("extension.script.compiler.on.the.fly.UserProxy", results);
//        Method setId = clazz.getMethod("setId", String.class);
//        Method setName = clazz.getMethod("setName", String.class);
//        Method setCreated = clazz.getMethod("setCreated", long.class);
//        // try instance:
//        Object obj = clazz.newInstance();
//        // get as proxy:
//        BeanProxy proxy = (BeanProxy) obj;
//        assertFalse(proxy.isDirty());
//        // set:
//        setId.invoke(obj, "A-123");
//        setName.invoke(obj, "Fly");
//        setCreated.invoke(obj, 123000999);
//        // get as user:
//        User user = (User) obj;
//        assertEquals("A-123", user.getId());
//        assertEquals("Fly", user.getName());
//        assertEquals(123000999, user.getCreated());
//        assertTrue(proxy.isDirty());
//    }
//
//    private void assertFalse(boolean dirty) {
//    }
//}