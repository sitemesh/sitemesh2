package testsuite.sitemesh;

import org.apache.catalina.*;
import org.apache.catalina.connector.*;
import org.apache.catalina.realm.*;
import org.apache.catalina.startup.*;

import java.io.File;

public class TomcatWebServer {// Instance variables:
    private String name = "sitemesh_tomcat_";
    private Embedded embedded;
    private Host baseHost;

    /**
     * Creates a new instance of EmbeddedTomcat
     */
    public TomcatWebServer(int port, String pathToWebApp) {

        try {
            embedded = new Embedded();
            embedded.setRealm(new MemoryRealm());

            // create an Engine
            Engine baseEngine = embedded.createEngine();

            // set Engine properties
            String hostName = name + "Host";

            baseEngine.setName(name + "Engine");
            baseEngine.setDefaultHost(hostName);

            baseHost = embedded.createHost(hostName, "webapps");
            baseEngine.addChild(baseHost);

            // RootContext
            addContext("", pathToWebApp);

            // add new Engine to set of Engine for embedded server
            embedded.addEngine(baseEngine);

            // create Connector
            Connector httpConnector = new Connector();  //this method shows errors better than the one below
            httpConnector.setPort(port);
//            Connector httpConnector = embedded.createConnector((java.net.InetAddress) null, port, false);

            // add new Connector to set of Connectors for embedded server, associated
            // with Engine
            embedded.addConnector(httpConnector);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Exception starting server " + e, e);
        }
    }

    public void start() {
        // start server
        try {
            embedded.start();
        }
        catch (org.apache.catalina.LifecycleException e) {
            e.printStackTrace();
            throw new RuntimeException("Cannot start server", e);
        }
    }

    public void stop() {
        try {
            embedded.stop();
        } catch (LifecycleException e) {
            e.printStackTrace();
            throw new RuntimeException("Cannot stop server", e);
        }
    }

    public Context addContext(String path, String docBase) {
        Context c = embedded.createContext(path, docBase);
        baseHost.addChild(c);

        return c;
    }

    public static void main(String[] args) {
        System.out.println("new File() = " + new File("").getAbsolutePath());
        new TomcatWebServer(9920, new File("").getAbsolutePath() + "/src/webapp").start();
    }
}