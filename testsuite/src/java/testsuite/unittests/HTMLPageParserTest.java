package testsuite.unittests;



import com.opensymphony.module.sitemesh.HTMLPage;
import com.opensymphony.module.sitemesh.PageParser;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.io.*;
import java.util.*;



/**
 * Test case for HTMLPageParser implementations. See parser-tests/readme.txt.
 * @author Joe Walnes
 */
public class HTMLPageParserTest extends TestCase {

    /**
     * This test case builds a custom suite, containing a collection of smaller suites (one for each file in src/parser-tests).
     */
    public static Test suite() throws IOException {
        TestSuite result = new TestSuite(HTMLPageParserTest.class.getName());

        File[] files = listParserTests(new File("src/parser-tests"));

        for (int i = 0; i < files.length; i++) {
            File file = files[i];
            TestSuite suiteForFile = new TestSuite(file.getName().replace('.','_'));
            suiteForFile.addTest(new HTMLPageParserTest(file, "testTitle"));
            suiteForFile.addTest(new HTMLPageParserTest(file, "testBody"));
            suiteForFile.addTest(new HTMLPageParserTest(file, "testHead"));
            suiteForFile.addTest(new HTMLPageParserTest(file, "testFullPage"));
            suiteForFile.addTest(new HTMLPageParserTest(file, "testProperties"));
            result.addTest(suiteForFile);
        }

        return result;
    }

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

    private HTMLPage page;

	private Map blocks;

	private String encoding;

	private File file;

	private String parserClass;

	public HTMLPageParserTest(File inputFile, String test) {
        super(test);
        file = inputFile;
		encoding = "UTF8";
		parserClass = "com.opensymphony.module.sitemesh.parser.FastPageParser";
	}



	protected void setUp() throws Exception {

		super.setUp();



		// read blocks from input file.

		this.blocks = readBlocks( new FileReader( file ) );



		// create PageParser and parse input block into HTMLPage object.

		String input = (String) blocks.get( "INPUT" );

		PageParser parser = (PageParser) Class.forName( parserClass ).newInstance();

		this.page = (HTMLPage) parser.parse( input.toCharArray());



	}



	public void testTitle() throws Exception {

		assertBlock( "TITLE", page.getTitle() );

	}



	public void testBody() throws Exception {

		StringWriter body = new StringWriter();

		page.writeBody( body );

		body.flush();

		assertBlock( "BODY", body.toString() );

	}



	public void testHead() throws Exception {

		StringWriter head = new StringWriter();

		page.writeHead( head );

		head.flush();

		assertBlock( "HEAD", head.toString() );

	}



	public void testFullPage() throws Exception {

		StringWriter fullPage = new StringWriter();

		page.writePage( fullPage );

		fullPage.flush();

		assertBlock( "INPUT", fullPage.toString() );

	}



	public void testProperties() throws Exception {

		Properties props = new Properties();

		{

			String propsString = (String) blocks.get( "PROPERTIES" );

			ByteArrayInputStream input = new ByteArrayInputStream( propsString.trim().getBytes( encoding ) );

			props.load( input );

		}

		String[] pageKeys = page.getPropertyKeys();

		assertEquals( file.getName() + " : Unexpected number of page properties", pageKeys.length, props.size() );

		for ( int i = 0; i < pageKeys.length; i++ ) {

			String pageKey = pageKeys[i];

			String blockValue = props.getProperty( pageKey );

			String pageValue = page.getProperty( pageKey );

			assertEquals( file.getName(), pageValue, blockValue );

		}

	}



	//-------------------------------------------------



	private void assertBlock( String blockName, String result ) throws Exception {

		String expected = (String) blocks.get( blockName );

		expected = expected.trim();

		result = result.trim();

		assertEquals( file.getName() + " : Block did not match", expected, result );

	}



	/**

	 * Read input to test and break down into blocks. See parser-tests/readme.txt

	 */

	private Map readBlocks( Reader input ) throws IOException {

		Map blocks;

		blocks = new HashMap();

		LineNumberReader reader = new LineNumberReader( input );

		String line;

		String blockName = null;

		StringBuffer blockContents = null;

		while ( ( line = reader.readLine() ) != null ) {

			if ( line.startsWith( "~~~ " ) && line.endsWith( " ~~~" ) ) {

				if ( blockName != null ) {

					blocks.put( blockName,  blockContents.toString() );

				}

				blockName = line.substring( 4, line.length() - 4 );

				blockContents = new StringBuffer();

			}

			else {

				if ( blockName != null ) {

					blockContents.append( line );

					blockContents.append( '\n' );

				}

			}

		}

		if ( blockName != null ) {

			blocks.put( blockName,  blockContents.toString() );

		}

		return blocks;

	}


}