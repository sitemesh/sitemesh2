package testsuite.unittests;

import junit.framework.TestSuite;
import junit.framework.Test;
import testsuite.unittests.repeatable.RepeatableTestDecorator;
import java.io.File;
import java.io.FilenameFilter;
import java.io.FileReader;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.List;

public class HTMLPageParserTestSuite extends TestSuite {

    public static Test suite() throws Exception {

        TestSuite suite = new TestSuite();
        File dir = new File("parser-tests");

        // get list of files to ignore
        LineNumberReader ignoreReader = new LineNumberReader(new FileReader(new File(dir, "ignore.txt")));

        final List ignoreFileNames = new ArrayList();

        String line;
        while ((line = ignoreReader.readLine()) != null) {
            ignoreFileNames.add(line);
        }
        // list all testXXX.txt files.        Object[] context = dir.listFiles(new FilenameFilter() {
            public boolean accept(File currentDir, String name) {
                return name.startsWith("test") && !ignoreFileNames.contains(name);
            }
        });

        suite.addTest(new RepeatableTestDecorator(new HTMLPageParserTest("testTitle"), context));
        suite.addTest(new RepeatableTestDecorator(new HTMLPageParserTest("testBody"), context));
        suite.addTest(new RepeatableTestDecorator(new HTMLPageParserTest("testHead"), context));
        suite.addTest(new RepeatableTestDecorator(new HTMLPageParserTest("testProperties"), context));
        suite.addTest(new RepeatableTestDecorator(new HTMLPageParserTest("testFullPage"), context));

        suite.addTest(new PathMapperTest("testHardening"));
        suite.addTest(new PathMapperTest("testFindExactKey"));
        suite.addTest(new PathMapperTest("testFindComplexKey"));
        suite.addTest(new PathMapperTest("testFindDefaultKey"));

        suite.addTest(new ConfigLoaderTest("testMappedNames"));
        suite.addTest(new ConfigLoaderTest("testDecoratorPresence"));
        suite.addTest(new ConfigLoaderTest("testDecorators"));
        return suite;
    }
}