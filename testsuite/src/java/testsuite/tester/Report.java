package testsuite.tester;

import junit.framework.TestListener;
import junit.framework.Test;
import junit.framework.AssertionFailedError;

import java.io.File;
import java.io.Writer;
import java.io.PrintWriter;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Date;

import testsuite.config.Server;
import com.opensymphony.util.TextUtils;

/**
 * TestListener for JUnit that outputs the result of the SiteMesh acceptance testsuite as an HTML matrix.
 *
 * @author Joe Walnes
 */
public class Report implements TestListener {
    private final File writeTo;

    public Report(File writeTo) {
        this.writeTo = writeTo;
    }

    private Map results = new HashMap();
    private List servers = new ArrayList();
    private List tests = new ArrayList();
    private String currentTestError;
    private boolean firstServer = true;
    private boolean anyFailures = false;
    private Server currentServer;

    private static class Tuple {
        private Server server;
        private Test test;

        public Tuple(Server server, Test test) {
            this.server = server;
            this.test = test;
        }

        public boolean equals(Object o) {
            Tuple tuple = (Tuple) o;
            return server.toString().equals(tuple.server.toString())
                    && test.toString().equals(tuple.test.toString());
        }

        public int hashCode() {
            return 29 * server.toString().hashCode() + test.toString().hashCode();
        }
    }

    public void startSuite() {
    }

    public void startServer(Server server) {
        servers.add(server);
        currentServer = server;
    }

    public void startTest(Test test) {
        if (firstServer) {
            tests.add(test);
        }
        currentTestError = null;
    }

    public void addError(Test test, Throwable throwable) {
        currentTestError = throwable.getMessage();
        anyFailures = true;
    }

    public void addFailure(Test test, AssertionFailedError assertionFailedError) {
        currentTestError = assertionFailedError.getMessage();
        anyFailures = true;
    }

    public void endTest(Test test) {
        results.put(new Tuple(currentServer, test), currentTestError);
    }

    public void endServer() {
        firstServer = false;
    }

    public void endSuite() {
        generateReport();
    }

    private void generateReport() {
        try {
            // Could use a template engine here, but it seemed overkill and I didn't see the need for another complexity. -jw
            PrintWriter writer = new PrintWriter(new FileOutputStream(writeTo));
            String title = "SiteMesh Acceptance Test Results : " + (anyFailures ? "FAILED" : "PASSED");
            writer.print("<html>");
            writer.print("<head>");
            writer.print("<style>");
            writer.print(".results { font-family: sans-serif; font-size: 10pt; }");
            writer.print(".title { font-weight: bold; }");
            writer.print(".server { font-weight: bold; text-align: center; background-color: #ccccff; }");
            writer.print(".testClass { background-color: #dddddd; font-weight: bold; }");
            writer.print(".testMethod { background-color: #eeeeee; padding-left: 10px; }");
            writer.print(".pass { background-color: #ccffcc; text-align: center; }");
            writer.print(".fail { background-color: #ffcccc; text-align: center; font-weight: bold;}");
            writer.print("</style>");
            writer.print("<title>" + title + "</title></head>");
            writer.print("<body>");
            writer.print("<table class='results'>");
            writer.print("<tr><td class='title'>" + title + "<br />" + new Date() + "</td>");
            for (Iterator iterator = servers.iterator(); iterator.hasNext();) {
                Server server = (Server) iterator.next();
                writer.print("<td class='server'>" + server.getName() + "<br />" + server.getVersion() + "</td>");
            }
            writer.print("</tr>");
            String lastTestClass = "";
            for (Iterator i1 = tests.iterator(); i1.hasNext();) {
                Test test = (Test) i1.next();
                String testName = test.toString();
                String testClass = testName.substring(testName.indexOf('(') + 1, testName.indexOf(')'));
                String testMethod = testName.substring(0, testName.indexOf('('));
                if (!lastTestClass.equals(testClass)) {
                    writer.print("<tr><td class='testClass' colspan='" + (servers.size() + 1) + "'>" + testClass + "</td></tr>");
                    lastTestClass = testClass;
                }
                writer.print("<tr><td class='testMethod'>" + testMethod + "</td>");
                for (Iterator i2 = servers.iterator(); i2.hasNext();) {
                    Server server = (Server) i2.next();
                    String error = (String) results.get(new Tuple(server, test));
                    if (error == null) {
                        writer.print("<td class='pass'>pass</td>");
                    } else {
                        writer.print("<td class='fail' title=\"" + TextUtils.htmlEncode(error)+ "\">FAIL</td>");
                    }
                }
                writer.print("</tr>");
            }
            writer.print("</table>");
            writer.print("</body>");
            writer.print("</html>");
            writer.close();
        } catch (FileNotFoundException e) {
            throw new RuntimeException("Cannot write to file " + writeTo, e);
        }
    }

}
