package testsuite.config;

import java.io.File;
import java.net.*;
import java.util.*;
import electric.xml.*;

/**
 * Read testsuite config from xml file to produce Application and Server objects.
 *
 * @author <a href="mailto:joe@truemesh.com">Joe Walnes</a>
 */
public class ConfigReader {

	private Application application;
	private List servers = new ArrayList();

	/**
	 * Create new config from xml file.
	 */
	public ConfigReader( File configFile ) throws ConfigException {
		try {

			// Parse file
			Element config = new Document( configFile ).getRoot();

			// Convert <application> tag to Application object
			{
				Element applicationTag = config.getElement( "application" );
				String name = applicationTag.getTextString( "name" );
				File warLocation = new File( configFile.getParentFile(), applicationTag.getTextString( "war-location" ) );
				File earLocation = new File( configFile.getParentFile(), applicationTag.getTextString( "ear-location" ) );

				application = new Application( name, warLocation, earLocation );
			}

			// Convert <server> tags to Server objects
			for ( Elements serverTags = config.getElements( "server" ); serverTags.hasMoreElements();  ) {
				Element serverTag = serverTags.next();

                if ( getBooleanAttribute(serverTag, "disabled" ) ) continue;

				String name     = serverTag.getTextString( "name" );
				String version  = serverTag.getTextString( "version" );
				URL baseURL     = new URL( serverTag.getTextString( "base-url" ) );
				File deployment = new File( configFile.getParentFile(), serverTag.getTextString( "deployment" ) );
				int type        = convertType( serverTag.getElement( "deployment" ).getAttributeValue( "type" ) );

				servers.add( new Server( name, version, baseURL, deployment, type ) );
			}

		}
		catch ( ParseException e ) {
			throw new ConfigException( "Cannot parse config file : " + configFile, e );
		}
		catch ( MalformedURLException e ) {
			throw new ConfigException( "Malformed URL", e );
		}
	}

    private boolean getBooleanAttribute(Element serverTag, String attname) {
        boolean result = false;
        Attribute attribute = serverTag.getAttribute( attname );
        if ( attribute != null ) {
            String value = attribute.getValue();
            if ( value != null && value.length() > 0 ) {
                char firstChar = value.toLowerCase().charAt( 0 );
                result = firstChar == 't' || firstChar == 'y' || firstChar == '1';
            }
        }
        return result;
    }

    /**
	 * Get application settings.
	 */
	public Application getApplication() {
		return application;
	}

	/**
	 * Get server settings. List contains Server objects.
	 */
	public List getServers() {
		return Collections.unmodifiableList( servers );
	}

	/**
	 * Convert "ear", "war", "unpack" to appropriate identifier.
	 */
	private int convertType( String s ) {
		if ( s == null ) s = "";
		s = s.trim().toLowerCase();
		if      ( s.equals( "ear" ) )    return Server.TYPE_EAR;
		else if ( s.equals( "unpack" ) ) return Server.TYPE_UNPACK;
		else                             return Server.TYPE_WAR;
	}

}