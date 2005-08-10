package testsuite.sitemesh;

import org.mortbay.http.SocketListener;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.servlet.WebApplicationContext;

import java.io.IOException;

public class WebServer {
    private Server server;

    public WebServer(int port, String pathToWebApp) throws IOException {
        server = new Server();
        SocketListener listener = new SocketListener();
        listener.setPort(port);
        server.addListener(listener);

        WebApplicationContext context = new WebApplicationContext(pathToWebApp);
        context.setContextPath("/");

        // This will load classes from primordial classloader first.
        // Avoids having to copy classes into WEB-INF/classes when running from the IDE.
        context.setClassLoaderJava2Compliant(true);

        server.addContext(null, context);
    }

    public void start() {
        try {
            server.start();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Cannot start server", e);
        }
    }

    public void stop() {
        try {
            server.stop();
        } catch (Exception e) {
            throw new RuntimeException("Cannot stop server", e);
        }
    }

    public static void main(String[] args) throws IOException {
        WebServer server = new WebServer(9912, "dist/webapp");
        server.start();
    }
}
