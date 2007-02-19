/*
 * Title:        FactoryException
 * Description:
 *
 * This software is published under the terms of the OpenSymphony Software
 * License version 1.1, of which a copy has been included with this
 * distribution in the LICENSE.txt file.
 */

package com.opensymphony.module.sitemesh.factory;

import java.io.PrintStream;
import java.io.PrintWriter;

/**
 * This RuntimeException is thrown by the Factory if it cannot initialize or perform
 * an appropriate function.
 *
 * @author <a href="mailto:joe@truemesh.com">Joe Walnes</a>
 * @version $Revision: 1.2 $
 */
public class FactoryException extends RuntimeException {
	protected Throwable exception = null;

	public FactoryException() {
		super();
	}

	public FactoryException(String msg) {
		super(msg);
	}

	public FactoryException(Exception e) {
		super();
		exception = e;
	}

	public FactoryException(String msg, Throwable e) {
		super(msg + ": " + e);
		exception = e;
	}

	/**
	 * Get the original cause of the Exception. Returns null if not known.
	 */
	public Throwable getRootCause() {
		return exception;
	}

    public void printStackTrace() {
  	    super.printStackTrace();
      	if (exception != null) {
            synchronized (System.err) {
    	        System.err.println("\nRoot cause:");
    		    exception.printStackTrace();
       	    }
	   	}
    }

    public void printStackTrace(PrintStream s) {
        super.printStackTrace(s);
      	if (exception != null) {
            synchronized (s) {
      	        s.println("\nRoot cause:");
        		exception.printStackTrace(s);
            }
        }
    }

	public void printStackTrace(PrintWriter s) {
  	    super.printStackTrace(s);
        if (exception != null) {
		    synchronized (s) {
          	    s.println("\nRoot cause:");
        		exception.printStackTrace(s);
            }
		}
    }
}