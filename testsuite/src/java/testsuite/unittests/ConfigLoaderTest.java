package testsuite.unittests;

import junit.framework.TestCase;

import java.io.*;
import java.util.*;

import com.opensymphony.module.sitemesh.mapper.ConfigLoader;

public class ConfigLoaderTest extends TestCase {

    public ConfigLoaderTest(String s) {
        super(s);
    }

    ConfigLoader cf = null;
    File tempConfigFile = null;

    protected void setUp() throws Exception {
        super.setUp();

        try {
            // create temp file
            tempConfigFile = File.createTempFile("decorators-test", ".xml");

            // write to temp file
            BufferedWriter out = new BufferedWriter(new FileWriter(tempConfigFile));
            out.write("<decorators defaultdir=\"/decorators\">");

            // new format test decorators

            out.write("   <decorator name=\"default\" page=\"default.jsp\">");
            out.write("       <pattern>/info/*</pattern>");
            out.write("       <url-pattern>   ");
            out.write("           /test/*");
            out.write("       </url-pattern>");
            out.write("       <url-pattern> </url-pattern>");
            out.write("       <url-pattern></url-pattern>");
            out.write("   </decorator>");

            out.write("   <decorator name=\"other\" page=\"/other.jsp\">");
            out.write("       <pattern>/other/*</pattern>");
            out.write("   </decorator>");

            out.write("   <decorator name=\"uri\" page=\"uri.jsp\" webapp=\"someapp\">");
            out.write("       <pattern>/uri/*</pattern>");
            out.write("   </decorator>");

            out.write("   <decorator name=\"rolebased\" page=\"/rolebased.jsp\" role=\"developer\">");
            out.write("       <pattern>/rolebased/*</pattern>");
            out.write("   </decorator>");

            // old format test decorator

            out.write("   <decorator>");
            out.write("       <decorator-name>old</decorator-name>");
            out.write("       <resource>/decorators/old.jsp</resource>");
            out.write("   </decorator>");

            out.write("   <decorator-mapping>");
            out.write("       <decorator-name>old</decorator-name>");
            out.write("       <url-pattern>/old/*</url-pattern>");
            out.write("   </decorator-mapping>");

            out.write("   <decorator-mapping>");
            out.write("       <decorator-name>old2</decorator-name>");
            out.write("       <url-pattern></url-pattern>");
            out.write("   </decorator-mapping>");

            out.write("</decorators>");
            out.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        cf = new ConfigLoader(tempConfigFile);
    }

    protected void tearDown() throws Exception {
        if (tempConfigFile != null) tempConfigFile.delete();
        cf = null;
    }

    public void testMappedNames() throws Exception {
        assertEquals(cf.getMappedName("/info/somepage.html"), "default");
        assertEquals(cf.getMappedName("/test/somepage.html"), "default");

        assertEquals(cf.getMappedName("/other/someotherpage.html"), "other");

        assertEquals(cf.getMappedName("/uri/somepage.html"), "uri");

        assertEquals(cf.getMappedName("/rolebased/someotherpage.html"), "rolebaseddeveloper");

        assertEquals(cf.getMappedName("/old/someoldpage.html"), "old");
    }

    public void testDecoratorPresence() throws Exception {
        assertNotNull(cf.getDecoratorByName("default"));
        assertNotNull(cf.getDecoratorByName("other"));
        assertNotNull(cf.getDecoratorByName("uri"));
        assertNotNull(cf.getDecoratorByName("rolebaseddeveloper"));
        assertNotNull(cf.getDecoratorByName("old"));
    }

    public void testDecorators() throws Exception {
        assertEquals(cf.getDecoratorByName("default").getName(), "default");
        assertEquals(cf.getDecoratorByName("default").getPage(), "/decorators/default.jsp");
        assertNull(cf.getDecoratorByName("default").getURIPath());
        assertNull(cf.getDecoratorByName("default").getRole());

        assertEquals(cf.getDecoratorByName("other").getName(), "other");
        assertEquals(cf.getDecoratorByName("other").getPage(), "/decorators/other.jsp");
        assertNull(cf.getDecoratorByName("other").getURIPath());
        assertNull(cf.getDecoratorByName("other").getRole());

        assertEquals(cf.getDecoratorByName("uri").getName(), "uri");
        assertEquals(cf.getDecoratorByName("uri").getPage(), "/decorators/uri.jsp");
        assertEquals(cf.getDecoratorByName("uri").getURIPath(), "/someapp");
        assertNull(cf.getDecoratorByName("uri").getRole());

        assertEquals(cf.getDecoratorByName("rolebaseddeveloper").getName(), "rolebased");
        assertEquals(cf.getDecoratorByName("rolebaseddeveloper").getPage(), "/decorators/rolebased.jsp");
        assertNull(cf.getDecoratorByName("rolebaseddeveloper").getURIPath());
        assertEquals(cf.getDecoratorByName("rolebaseddeveloper").getRole(), "developer");

        assertEquals(cf.getDecoratorByName("old").getName(), "old");
        assertEquals(cf.getDecoratorByName("old").getPage(), "/decorators/old.jsp");
        assertNull(cf.getDecoratorByName("old").getURIPath());
        assertNull(cf.getDecoratorByName("old").getRole());
    }
}