package testsuite.sitemesh;

import org.mortbay.http.SocketListener;
import org.mortbay.jetty.Server;

import java.io.IOException;

public class WebServer {
    private Server server;

    public WebServer(int port, String pathToWebApp) throws IOException {
        server = new Server();
        SocketListener listener = new SocketListener();
        listener.setPort(port);
        server.addListener(listener);
        try {
            server.addWebApplication("/", pathToWebApp);
        } catch (IOException e) {
            throw new RuntimeException("Cannot add web-app: " + pathToWebApp, e);
        }
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
}
