package com.opensymphony.module.sitemesh.mapper;

import junit.framework.TestCase;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

public class ConfigLoaderTest extends TestCase {

    private ConfigLoader configLoader;
    private File tempConfigFile;

    protected void setUp() throws Exception {
        super.setUp();

        // create temp file
        tempConfigFile = File.createTempFile("decorators-test", ".xml");
        tempConfigFile.deleteOnExit();

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
        configLoader = new ConfigLoader(tempConfigFile);
    }

    protected void tearDown() throws Exception {
        if (tempConfigFile != null) tempConfigFile.delete();
        configLoader = null;
    }

    public void testMappedNames() throws Exception {
        assertEquals(configLoader.getMappedName("/info/somepage.html"), "default");
        assertEquals(configLoader.getMappedName("/test/somepage.html"), "default");

        assertEquals(configLoader.getMappedName("/other/someotherpage.html"), "other");

        assertEquals(configLoader.getMappedName("/uri/somepage.html"), "uri");

        assertEquals(configLoader.getMappedName("/rolebased/someotherpage.html"), "rolebaseddeveloper");

        assertEquals(configLoader.getMappedName("/old/someoldpage.html"), "old");
    }

    public void testDecoratorPresence() throws Exception {
        assertNotNull(configLoader.getDecoratorByName("default"));
        assertNotNull(configLoader.getDecoratorByName("other"));
        assertNotNull(configLoader.getDecoratorByName("uri"));
        assertNotNull(configLoader.getDecoratorByName("rolebaseddeveloper"));
        assertNotNull(configLoader.getDecoratorByName("old"));
    }

    public void testDecorators() throws Exception {
        assertEquals(configLoader.getDecoratorByName("default").getName(), "default");
        assertEquals(configLoader.getDecoratorByName("default").getPage(), "/decorators/default.jsp");
        assertNull(configLoader.getDecoratorByName("default").getURIPath());
        assertNull(configLoader.getDecoratorByName("default").getRole());

        assertEquals(configLoader.getDecoratorByName("other").getName(), "other");
        assertEquals(configLoader.getDecoratorByName("other").getPage(), "/decorators/other.jsp");
        assertNull(configLoader.getDecoratorByName("other").getURIPath());
        assertNull(configLoader.getDecoratorByName("other").getRole());

        assertEquals(configLoader.getDecoratorByName("uri").getName(), "uri");
        assertEquals(configLoader.getDecoratorByName("uri").getPage(), "/decorators/uri.jsp");
        assertEquals(configLoader.getDecoratorByName("uri").getURIPath(), "/someapp");
        assertNull(configLoader.getDecoratorByName("uri").getRole());

        assertEquals(configLoader.getDecoratorByName("rolebaseddeveloper").getName(), "rolebased");
        assertEquals(configLoader.getDecoratorByName("rolebaseddeveloper").getPage(), "/decorators/rolebased.jsp");
        assertNull(configLoader.getDecoratorByName("rolebaseddeveloper").getURIPath());
        assertEquals(configLoader.getDecoratorByName("rolebaseddeveloper").getRole(), "developer");

        assertEquals(configLoader.getDecoratorByName("old").getName(), "old");
        assertEquals(configLoader.getDecoratorByName("old").getPage(), "/decorators/old.jsp");
        assertNull(configLoader.getDecoratorByName("old").getURIPath());
        assertNull(configLoader.getDecoratorByName("old").getRole());
    }
}