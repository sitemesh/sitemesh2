package testsuite.config;

import java.io.File;
import java.net.URL;

/**
 * Representation of a server to deploy a web-app to and then test.
 *
 * @author <a href="mailto:joe@truemesh.com">Joe Walnes</a>
 */
public class Server {

	/**
	 * Server requires web-app to be deployed as a single .war file.
	 */
	public static final int TYPE_WAR    = 0;

	/**
	 * Server requires web-app to be deployed as a module in a .ear file.
	 */
	public static final int TYPE_EAR    = 1;

	/**
	 * Server requires web-app to unpacked into a directory.
	 */
	public static final int TYPE_UNPACK = 2;

	private String name, version;
	private URL baseURL;
	private File deployment;
	private int type;

	public Server( String name, String version, URL baseURL, File deployment, int type ) {
		this.name = name;
		this.version = version;
		this.baseURL = baseURL;
		this.deployment = deployment;
		this.type = type;
	}

	/**
	 * Name of server (e.g. Orion)
	 */
	public String getName() { return name; }

	/**
	 * Version of server (e.g. 1.5.2)
	 */
	public String getVersion() { return version; }

	/**
	 * BaseURL that web-app is deployed to (e.g. http://localhost:1234/myapp)
	 */
	public URL getBaseURL() { return baseURL; }

	/**
	 * Where to deploy file to (e.g. c:\someserver\webapps\mywebapp.war)
	 */
	public File getDeployment() { return deployment; }

	/**
	 * Type of deployment.
	 *
	 * @see TYPE_WAR
	 * @see TYPE_EAR
	 * @see TYPE_UNPACK
	 */
	public int getType() { return type; }

	public String toString() {
		StringBuffer result = new StringBuffer();
		result.append( "{ Server : \n  name=" );
		result.append( name );
		result.append( "\n  version=" );
		result.append( version );
		result.append( "\n  baseURL=" );
		result.append( baseURL );
		result.append( "\n  deployment=" );
		result.append( deployment );
		result.append( "\n  type=" );
		result.append( ( new String[] { "war", "ear", "unpack" } )[type] );
		result.append( "\n}" );
		return result.toString();
	}

}
