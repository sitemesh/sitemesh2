package testsuite.unittests;

import junit.framework.TestCase;

import java.io.*;
import java.util.*;

import com.opensymphony.module.sitemesh.mapper.PathMapper;

public class PathMapperTest extends TestCase {

	public PathMapperTest( String s ) {
		super(s);
	}

    PathMapper pm = null;

    protected void setUp() throws Exception {
        super.setUp();

        pm = new PathMapper();

        // exact matches come first
        pm.put("exact1", "/myexactfile.html");
        pm.put("exact2", "/mydir/myexactfile.html");
        pm.put("exact3", "/mydir/myexactfile.jsp");
        pm.put("exact4", "/mydir/dodo");

        // then the complex matches
        pm.put("complex1", "/mydir/*");
        pm.put("complex2", "/mydir/otherdir/*.jsp");
        pm.put("complex3", "/otherdir/*.??p");
        pm.put("complex4", "*.xml");
        pm.put("complex5", "/*/admin/*.??ml");
        pm.put("complex6", "/*/complexx/a*b.x?tml");

        // if all the rest fails, use the default matches
        pm.put("default", "*");
    }

    public void testHardening() throws Exception {
        PathMapper bad = new PathMapper();
        bad.put(null, null);
        assertEquals(null, bad.get(null));
        assertEquals(null, bad.get(""));
        assertEquals(null, bad.get("/somenonexistingpath"));
    }

    public void testFindExactKey() throws Exception {
        assertEquals("exact1", pm.get("/myexactfile.html"));
        assertEquals("exact2", pm.get("/mydir/myexactfile.html"));
        assertEquals("exact3", pm.get("/mydir/myexactfile.jsp"));
        assertEquals("exact4", pm.get("/mydir/dodo"));
	}

	public void testFindComplexKey() throws Exception {
	    assertEquals("complex1", pm.get("/mydir/"));
        assertEquals("complex1", pm.get("/mydir/test1.xml"));
        assertEquals("complex1", pm.get("/mydir/test321.jsp"));
        assertEquals("complex1", pm.get("/mydir/otherdir"));

        assertEquals("complex2", pm.get("/mydir/otherdir/test321.jsp"));

	    assertEquals("complex3", pm.get("/otherdir/test2.jsp"));
        assertEquals("complex3", pm.get("/otherdir/test2.bpp"));

	    assertEquals("complex4", pm.get("/somedir/one/two/some/deep/file/test.xml"));
        assertEquals("complex4", pm.get("/somedir/321.jsp.xml"));

        assertEquals("complex5", pm.get("/mydir/otherdir/admin/myfile.html"));
        assertEquals("complex5", pm.get("/mydir/somedir/admin/text.html"));

        assertEquals("complex6", pm.get("/mydir/complexx/a-some-test-b.xctml"));
        assertEquals("complex6", pm.get("/mydir/complexx/a b.xhtml"));
        assertEquals("complex6", pm.get("/mydir/complexx/a___b.xhtml"));
    }

    public void testFindDefaultKey() throws Exception {
        assertEquals("default", pm.get(null));
        assertEquals("default", pm.get("/"));
        assertEquals("default", pm.get("/*"));
        assertEquals("default", pm.get("*"));
        assertEquals("default", pm.get("blah.txt"));
        assertEquals("default", pm.get("somefilewithoutextension"));
        assertEquals("default", pm.get("/file_with_underscores-and-dashes.test"));
        assertEquals("default", pm.get("/tuuuu*/file.with.dots.test.txt"));
    }
}