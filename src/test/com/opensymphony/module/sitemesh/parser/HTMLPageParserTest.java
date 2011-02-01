package com.opensymphony.module.sitemesh.parser;

import com.opensymphony.module.sitemesh.HTMLPage;
import com.opensymphony.module.sitemesh.Page;
import com.opensymphony.module.sitemesh.PageParser;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.framework.Test;

/**
 * Test case for HTMLPageParser implementations. See parser-tests/readme.txt.
 *
 * @author Joe Walnes
 */
public class HTMLPageParserTest extends TestCase {

    private static final Pattern PARSER_PATTERN = Pattern.compile("parser\\.(.+)\\.class");

    /**
     * This test case builds a custom suite, containing a collection of smaller suites (one for each file in src/parser-tests).
     */
    public static Test suite() throws Exception {
        TestSuite result = new TestSuite(HTMLPageParserTest.class.getName());

        Properties props = new Properties();
        props.load(new FileInputStream("src/parser-tests/parsers.properties"));

        Collection<String> parsers = new ArrayList<String>();
        for (String key : props.stringPropertyNames()) {
            Matcher matcher = PARSER_PATTERN.matcher(key);
            if (matcher.matches()) {
                parsers.add(matcher.group(1));
            }
        }

        for (String p : parsers) {
            String name = props.getProperty("parser." + p + ".class");
            Class<? extends PageParser> parserClass = Class.forName(name).asSubclass(PageParser.class);
            PageParser parser = parserClass.newInstance();

            String filesPath = props.getProperty("parser." + p + ".tests", "src/parser-tests");
            List<File> files = new ArrayList<File>(Arrays.asList(listParserTests(new File(filesPath))));
            Collections.sort(files);

            TestSuite suiteForParser = new TestSuite(name);
            for (File file : files) {
                TestSuite suiteForFile = new TestSuite(file.getName().replace('.', '_'));
                suiteForFile.addTest(new HTMLPageParserTest(parser, file, "testTitle"));
                suiteForFile.addTest(new HTMLPageParserTest(parser, file, "testBody"));
                suiteForFile.addTest(new HTMLPageParserTest(parser, file, "testHead"));
                suiteForFile.addTest(new HTMLPageParserTest(parser, file, "testFullPage"));
                suiteForFile.addTest(new HTMLPageParserTest(parser, file, "testProperties"));
                suiteForParser.addTest(suiteForFile);
            }
            result.addTest(suiteForParser);
        }

        return result;
    }

    private Page page;
    private Map blocks;
    private String encoding;
    private final PageParser parser;
    private File file;

    public HTMLPageParserTest(PageParser parser, File inputFile, String test) {
        super(test);
        this.parser = parser;
        file = inputFile;
        encoding = "UTF8";
    }

    protected void setUp() throws Exception {
        super.setUp();
        // read blocks from input file.
        this.blocks = readBlocks(new FileReader(file));
        // create PageParser and parse input block into HTMLPage object.
        String input = (String) blocks.get("INPUT");
        this.page = parser.parse(input.toCharArray());
    }

    public void testTitle() throws Exception {
        assertBlock("TITLE", page.getTitle());
    }

    public void testBody() throws Exception {
        StringWriter body = new StringWriter();
        page.writeBody(body);
        body.flush();
        assertBlock("BODY", body.toString());
    }

    public void testHead() throws Exception {
        String head;
        if (page instanceof HTMLPage) {
            StringWriter headWriter = new StringWriter();
            ((HTMLPage)page).writeHead(headWriter);
            headWriter.flush();
            head = headWriter.toString();
        } else {
            head = "";
        }
        assertBlock("HEAD", head.toString());
    }

    public void testFullPage() throws Exception {
        StringWriter fullPage = new StringWriter();
        page.writePage(fullPage);
        fullPage.flush();
        assertBlock("INPUT", fullPage.toString());
    }

    public void testProperties() throws Exception {
        Properties props = new Properties();
        String propsString = (String) blocks.get("PROPERTIES");
        ByteArrayInputStream input = new ByteArrayInputStream(propsString.trim().getBytes(encoding));
        props.load(input);

        String[] pageKeys = page.getPropertyKeys();
        assertEquals(file.getName() + " : Unexpected number of page properties [" + join(pageKeys) + "]",
                props.size(), pageKeys.length);

        for (int i = 0; i < pageKeys.length; i++) {
            String pageKey = pageKeys[i];
            String blockValue = props.getProperty(pageKey);
            String pageValue = page.getProperty(pageKey);
            assertEquals(file.getName(),
                    blockValue == null ? null : blockValue.trim(),
                    pageValue == null ? null : pageValue.trim());
        }
    }

    private String join(String[] values) {
        StringBuffer result = new StringBuffer();
        for (int i = 0; i < values.length; i++) {
            if (i > 0) {
                result.append(',');
            }
            result.append(values[i]);
        }
        return result.toString();
    }

    //-------------------------------------------------

    private static File[] listParserTests(File dir) throws IOException {
        // get list of files to ignore
        LineNumberReader ignoreReader = new LineNumberReader(new FileReader(new File(dir, "ignore.txt")));
        final List ignoreFileNames = new ArrayList();
        String line;
        while ((line = ignoreReader.readLine()) != null) {
            ignoreFileNames.add(line);
        }
        return dir.listFiles(new FilenameFilter() {
            public boolean accept(File currentDir, String name) {
                return name.startsWith("test") && !ignoreFileNames.contains(name);
            }
        });
    }

    private void assertBlock(String blockName, String result) throws Exception {
        String expected = (String) blocks.get(blockName);
        assertEquals(file.getName() + " : Block did not match", expected.trim(), result.trim());
    }

    /**
     * Read input to test and break down into blocks. See parser-tests/readme.txt
     */
    private Map readBlocks(Reader input) throws IOException {
        Map blocks = new HashMap();
        LineNumberReader reader = new LineNumberReader(input);
        String line;
        String blockName = null;
        StringBuffer blockContents = null;
        while ((line = reader.readLine()) != null) {
            if (line.startsWith("~~~ ") && line.endsWith(" ~~~")) {
                if (blockName != null) {
                    blocks.put(blockName, blockContents.toString());
                }
                blockName = line.substring(4, line.length() - 4);
                blockContents = new StringBuffer();
            } else {
                if (blockName != null) {
                    blockContents.append(line);
                    blockContents.append('\n');
                }
            }
        }

        if (blockName != null) {
            blocks.put(blockName, blockContents.toString());
        }

        return blocks;
    }

}

