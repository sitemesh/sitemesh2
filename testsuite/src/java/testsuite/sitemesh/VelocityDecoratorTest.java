package testsuite.sitemesh;

import com.meterware.httpunit.WebResponse;
import electric.xml.Document;
import testsuite.tester.WebTest;

/**
 * @author <a href="mailto:pathos@pandora.be">Mathias Bogaert</a>
 * @version $Revision: 1.1 $
 */
public class VelocityDecoratorTest  extends WebTest {
    public void testVelocityDecoratedPage() throws Exception {
		WebResponse rs = wc.getResponse( server.getBaseURL() + "/velocity/velocity.jsp" );
		Document doc = getDocument( rs );
		assertEquals( "[:: Simple Velocity Page ::]", rs.getTitle() );
		assertEquals( "Hello Velocity world", doc.getElementWithId( "p1" ).getText().toString() );
		assertEquals( "footer", doc.getElementWithId( "footer" ).getText().toString() );
		assertEquals( "Simple Velocity Page", doc.getElementWithId( "header" ).getText().toString() );
	}
}
