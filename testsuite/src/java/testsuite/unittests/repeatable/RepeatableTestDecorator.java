package testsuite.unittests.repeatable;

import junit.extensions.TestDecorator;
import junit.framework.TestResult;
import testsuite.unittests.repeatable.RepeatableTest;

/**
 * JUnit TestDecorator that decorates a standard TestCase that implements the
 * RepeatableTest interface and runs the test X many times.
 *
 * For each run, a context object is passed into the TestCase because it's
 * likely that you will want something to differ slightly for each test run.
 * This object can be anything such as a number, filename, expect result object,
 * etc. Pass across an array of objects to the constructor, and the test shall
 * be repeated for each element in the array.
 *
 * To use, create a TestSuite and add your existing tests to it using the decorator.
 *
 * <pre>
 * public class MyTestSuite extends TestSuite {
 *   public static Test suite() {
 *     TestSuite suite = new TestSuite();
 *     Object[] context = new Object[] { "one", "two", "three" }
 *     RepeatableTest test = new MyTest();
 *     suite.addTest( new RepeatableTestDecorator( test, context ) );
 *     return suite;
 *   }
 * }
 * </pre>
 *
 * This class is different to junit.extensions.RepeatedTest in that it passes
 * a new context across each time.
 *
 * @author <a href="mailto:joe@truemesh.com">Joe Walnes</a>
 *
 * @see testsuite.unittests.repeatable.RepeatableTest
 * @see junit.extensions.TestDecorator
 */
public class RepeatableTestDecorator extends TestDecorator {

	private Object[] contexts;

	/**
	 * @param test TestCase to be repeated.
	 * @param contexts List of contexts to iterate through for each test.
	 */
  public RepeatableTestDecorator( RepeatableTest test, Object[] contexts ) {
    super( test );
	  this.contexts = contexts;
  }

	/**
	 * Do not call directly - called by TestRunner
	 */
	public void run( TestResult testResult ) {
		for ( int i = 0; i < contexts.length; i++ ) {
			RepeatableTest test = (RepeatableTest) getTest();
			test.setCurrentContext( contexts[i] );
			super.run( testResult );
		}
	}

	public int countTestCases() {
		return super.countTestCases() * contexts.length;
	}

}
