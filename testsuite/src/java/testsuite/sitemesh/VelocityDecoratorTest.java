package testsuite.sitemesh;

import com.meterware.httpunit.WebResponse;
import electric.xml.Document;
import testsuite.tester.WebTest;

/**
 * @author <a href="mailto:pathos@pandora.be">Mathias Bogaert</a>
 * @version $Revision: 1.3 $
 */
public class VelocityDecoratorTest  extends WebTest {
    public void testVelocityDecoratedPage() throws Exception {
		WebResponse rs = wc.getResponse( baseUrl + "/velocity/velocity.jsp" );
		Document doc = getDocument( rs );
		assertEquals( "[:: Simple Velocity Page ::]", rs.getTitle() );
		assertEquals( "Hello Velocity world", doc.getElementWithId( "p1" ).getText().toString() );
		assertEquals( "footer", doc.getElementWithId( "footer" ).getText().toString() );
		assertEquals( "Simple Velocity Page", doc.getElementWithId( "header" ).getText().toString() );
        assertEquals( "\u0126\u0118\u0139\u0139\u0150", doc.getElementWithId( "i18n" ).getText().toString() );
	}
}
