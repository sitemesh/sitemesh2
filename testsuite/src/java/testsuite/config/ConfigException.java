package testsuite.config;

/**
 * Exception thrown when reading config.
 *
 * @author <a href="mailto:joe@truemesh.com">Joe Walnes</a>
 */
public class ConfigException extends Exception {

	private Exception cause;

	public ConfigException( String msg, Exception cause ) {
		super( msg );
		this.cause = cause;
	}

	public Throwable getCause() {
		return cause;
	}

}