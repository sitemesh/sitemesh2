package testsuite.unittests.repeatable;

import junit.framework.Test;

/**
 * Interface that needs to be implemented by any JUnit TestCase that needs
 * to be repeated using the RepeatableTestDecorator.
 *
 * @author <a href="mailto:joe@truemesh.com">Joe Walnes</a>
 *
 * @see testsuite.unittests.repeatable.RepeatableTestDecorator
 */
public interface RepeatableTest extends Test {

	/**
	 * At the beginning of each test iteration, the context object is set.
	 *
	 * @param context Context object for this test iteration.
	 */
	void setCurrentContext( Object context );

}
