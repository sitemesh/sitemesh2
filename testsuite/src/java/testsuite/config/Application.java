package testsuite.config;

import java.io.File;

/**
 * Representation of application to be deployed and tested.
 *
 * @author <a href="mailto:joe@truemesh.com">Joe Walnes</a>
 */
public class Application {

	private String name;
	private File warLocation, earLocation;

	public Application( String name, File warLocation, File earLocation ) {
		this.name = name;
		this.warLocation = warLocation;
		this.earLocation = earLocation;
	}

	/**
	 * Name of application (e.g. SiteMesh)
	 */
	public String getName() { return name; }

	/**
	 * Where web-app .war is located.
	 */
	public File getWarLocation() { return warLocation; }

	/**
	 * Where .ear containing web-app is located.
	 */
	public File getEarLocation() { return earLocation; }

	public String toString() {
		StringBuffer result = new StringBuffer();
		result.append( "{ Application : \n  name=" );
		result.append( name );
		result.append( "\n  warLocation=" );
		result.append( warLocation );
		result.append( "\n  earLocation=" );
		result.append( earLocation );
		result.append( "\n}" );
		return result.toString();
	}

}
