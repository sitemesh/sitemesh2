package testsuite.deploy;

import java.io.*;
import java.util.*;
import java.util.jar.*;
import testsuite.config.*;

/**
 * Class to deploy application to all the servers. To use, just call constructor with config.
 *
 * @author <a href="mailto:joe@truemesh.com">Joe Walnes</a>
 */
public class DeployWebApps {

	private static final String LINE       = "----------------------------------------------------";
	private static final String DOUBLELINE = "====================================================";
	private static final String INDENT     = "  ";

	/**
	 * DeployWebApps using configuarion.
	 */
	public DeployWebApps( ConfigReader config ) throws IOException {

		Application application = config.getApplication();

		print( DOUBLELINE );
		print( "Deploying " + application.getName() + " to servers" );

		// loop through each server for deployment
		for( Iterator servers = config.getServers().iterator(); servers.hasNext(); ) {
			Server server = (Server) servers.next();
			print( LINE );
			print( server.getName() + " version " + server.getVersion() );

			switch ( server.getType() ) {

				// copy warfile
				case Server.TYPE_WAR:
					print( INDENT + "copying warfile to " + server.getDeployment() );
					copyFile( application.getWarLocation(), server.getDeployment() );
					break;

				// copy earfile
				case Server.TYPE_EAR:
					print( INDENT + "copying earfile to " + server.getDeployment() );
					copyFile( application.getEarLocation(), server.getDeployment() );
					break;

				// unpack warfile
				case Server.TYPE_UNPACK:
					print( INDENT + "unpacking warfile to " + server.getDeployment() );
					//delTree( server.getDeployment() );
					unPack( application.getWarLocation(), server.getDeployment() );
					break;

			}

		}

		print( DOUBLELINE );

	}

	/**
	 * Print something to stdout
	 */
	private void print( String s ) {
		System.out.println( s );
	}

	/**
	 * Copy a file.
	 */
	private void copyFile( File src, File dest ) throws IOException {
		FileInputStream in = new FileInputStream( src );
		FileOutputStream out = new FileOutputStream( dest );
		for (int c; (c = in.read()) != -1; out.write(c)); // empty loop
		in.close();
		out.close();
	}

	/**
	 * Delete a directory and its contents recursively. Use with care :)
	 */
	private void delTree( File file ) throws IOException {
		if ( file.exists() ) {
			File[] children = file.listFiles();
			for ( int i=0; children != null && i < children.length; i++) delTree( children[i] );
			file.delete();
		}
	}

	/**
	 * Unpack war file as a directory.
	 */
	private void unPack( File war, File dir ) throws IOException {
		JarFile jar = new JarFile( war );
		dir.mkdir();
		for ( Enumeration entries = jar.entries(); entries.hasMoreElements(); ) {
			JarEntry entry = (JarEntry) entries.nextElement();
			File file = new File( dir, entry.getName() );
			if ( entry.isDirectory() ) {
				if ( !file.exists() ) file.mkdir();
			}
			else {
				InputStream in = jar.getInputStream( entry );
				FileOutputStream out = new FileOutputStream( file );
				for (int c; (c = in.read()) != -1; out.write(c)); // empty loop
				in.close();
				out.close();
			}
		}
	}

	/**
	 * Command line access.
	 * Load config from file. If cmd line arg given, use that file, otherwise tests.xml
   */
	public static void main( String[] args ) throws ConfigException, IOException {

		File configFile = new File( args.length == 0 ? "tests.xml" : args[0] );
		ConfigReader config = new ConfigReader( configFile );

		DeployWebApps d = new DeployWebApps( config );

	}

}