package com.opensymphony.module.sitemesh.mapper;

import junit.framework.TestCase;
import junit.framework.Assert;

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
        Assert.assertEquals(configLoader.getMappedName("/info/somepage.html"), "default");
        Assert.assertEquals(configLoader.getMappedName("/test/somepage.html"), "default");

        Assert.assertEquals(configLoader.getMappedName("/other/someotherpage.html"), "other");

        Assert.assertEquals(configLoader.getMappedName("/uri/somepage.html"), "uri");

        Assert.assertEquals(configLoader.getMappedName("/rolebased/someotherpage.html"), "rolebaseddeveloper");

        Assert.assertEquals(configLoader.getMappedName("/old/someoldpage.html"), "old");
    }

    public void testDecoratorPresence() throws Exception {
        Assert.assertNotNull(configLoader.getDecoratorByName("default"));
        Assert.assertNotNull(configLoader.getDecoratorByName("other"));
        Assert.assertNotNull(configLoader.getDecoratorByName("uri"));
        Assert.assertNotNull(configLoader.getDecoratorByName("rolebaseddeveloper"));
        Assert.assertNotNull(configLoader.getDecoratorByName("old"));
    }

    public void testDecorators() throws Exception {
        Assert.assertEquals(configLoader.getDecoratorByName("default").getName(), "default");
        Assert.assertEquals(configLoader.getDecoratorByName("default").getPage(), "/decorators/default.jsp");
        Assert.assertNull(configLoader.getDecoratorByName("default").getURIPath());
        Assert.assertNull(configLoader.getDecoratorByName("default").getRole());

        Assert.assertEquals(configLoader.getDecoratorByName("other").getName(), "other");
        Assert.assertEquals(configLoader.getDecoratorByName("other").getPage(), "/decorators/other.jsp");
        Assert.assertNull(configLoader.getDecoratorByName("other").getURIPath());
        Assert.assertNull(configLoader.getDecoratorByName("other").getRole());

        Assert.assertEquals(configLoader.getDecoratorByName("uri").getName(), "uri");
        Assert.assertEquals(configLoader.getDecoratorByName("uri").getPage(), "/decorators/uri.jsp");
        Assert.assertEquals(configLoader.getDecoratorByName("uri").getURIPath(), "/someapp");
        Assert.assertNull(configLoader.getDecoratorByName("uri").getRole());

        Assert.assertEquals(configLoader.getDecoratorByName("rolebaseddeveloper").getName(), "rolebased");
        Assert.assertEquals(configLoader.getDecoratorByName("rolebaseddeveloper").getPage(), "/decorators/rolebased.jsp");
        Assert.assertNull(configLoader.getDecoratorByName("rolebaseddeveloper").getURIPath());
        Assert.assertEquals(configLoader.getDecoratorByName("rolebaseddeveloper").getRole(), "developer");

        Assert.assertEquals(configLoader.getDecoratorByName("old").getName(), "old");
        Assert.assertEquals(configLoader.getDecoratorByName("old").getPage(), "/decorators/old.jsp");
        Assert.assertNull(configLoader.getDecoratorByName("old").getURIPath());
        Assert.assertNull(configLoader.getDecoratorByName("old").getRole());
    }
}