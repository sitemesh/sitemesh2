*************************************
** OpenSymphony SiteMesh 2.0.2     **
*************************************

SiteMesh is a web-page layout system that can be used to abstract common look
and feel from the functionality of a web-application and to assemble large
webpages from smaller components. Pages and components can have meta-data
extracted from them (such as body, title and meta-tags) which can be used by
decorators (skins) that are applied.

SiteMesh won't tread on your toes or force you to work in a certain way (except
for cleaner) - you install and carry on working as before. It seamlessly fits in
with existing frameworks.

Forget the hype - just try it! You'll be impressed with how it can simplify
things.

--------------------------
-- Obtaining            --
--------------------------

The latest version of SiteMesh can be obtained from:

    http://www.opensymphony.com/sitemesh/
        or
    https://sitemesh.dev.java.net/

--------------------------
-- Requirements         --
--------------------------

SiteMesh requires a Java Servlet container conforming to the Servlet 2.3
specification. Versions prior to 2.3 are not enough.

Currently known containers that support this and SiteMesh was tested with:

* Orion 1.5.4 and up                    - http://www.orionserver.com
* Resin 2.1.11                          - http://www.caucho.com
* Tomcat 4.0 and 4.1                    - http://jakarta.apache.org/tomcat
* WebLogic 7.0 SP2, 8.1 and 8.1 SP2     - http://www.bea.com
* WebSphere 5.0                         - http://www.ibm.com
* Oracle OC4J                           - http://www.oracle.com
* Jetty 4                               - http://jetty.mortbay.org

--------------------------
-- Installation         --
--------------------------

* Copy sitemesh.jar to the WEB-INF/lib/ directory of your web-app.

* Copy sitemesh-decorator.tld and sitemesh-page.tld to the WEB-INF/ directory of your web-app.

* OPTIONAL: Copy sitemesh.xml to the WEB-INF/ directory if you need to specify a custom
  decorator mapper configuration then the default configuration.

* Add the following to WEB-INF/web.xml:

    <filter>
        <filter-name>sitemesh</filter-name>
        <filter-class>com.opensymphony.module.sitemesh.filter.PageFilter</filter-class>
    </filter>

    <filter-mapping>
        <filter-name>sitemesh</filter-name>
        <url-pattern>/*</url-pattern>
    </filter-mapping>

    <taglib>
        <taglib-uri>sitemesh-decorator</taglib-uri>
        <taglib-location>/WEB-INF/sitemesh-decorator.tld</taglib-location>
    </taglib>

    <taglib>
        <taglib-uri>sitemesh-page</taglib-uri>
        <taglib-location>/WEB-INF/sitemesh-page.tld</taglib-location>
    </taglib>

--------------------------
-- Getting started      --
--------------------------

Ok, let's assume you have some basic JSPs already on the site.
These should contain vanilla HTML.

If you don't, here's a JSP to get you started (test.jsp).

    <html>
        <head>
            <title>Hello world</title>
        </head>
        <body>
            <p>Today is <%= new java.util.Date() %>.</p>
        </body>
    </html>

Once you have some content (preferably more imaginative than the example above),
a decorator should be created (decorator.jsp).

    <%@ taglib uri="sitemesh-decorator" prefix="decorator" %>
    <html>
        <head>
            <title>My Site - <decorator:title default="Welcome!" /></title>
            <decorator:head />
        </head>
        <body>
            <decorator:body />
        </body>
    </html>

Now you need tell SiteMesh about that decorator and when to use it. Create the
file WEB-INF/decorators.xml:

    <decorators>

        <decorator name="mydecorator" page="/decorator.jsp">
            <pattern>/*</pattern>
        </decorator>

    </decorators>

Access your original JSP (test.jsp) though your web-browser and it should look
pretty normal. Now if you add some styling to your decorator it shall
automatically be applied to all the other pages in your web-app.

You can define as many decorators as you want in decorators.xml. Example:

    <decorators defaultdir="/decorators">

        <decorator name="default" page="default.jsp">
            <pattern>/*</pattern>
        </decorator>

        <decorator name="anotherdecorator" page="decorator2.jsp">
            <pattern>/subdir/*</pattern>
        </decorator>

        <decorator name="htmldecorator" page="html.jsp">
            <pattern>*.html</pattern>
            <pattern>*.htm</pattern>
        </decorator>

        <decorator name="none">
            <!-- These files will not get decorated. -->
            <pattern>/anotherdir/*</pattern>
        </decorator>

    </decorators>

--------------------------
-- Further support      --
--------------------------

You get the idea. Play around. See the SiteMesh website for
full documentation.

    http://www.opensymphony.com/sitemesh/

Mailing list subscription info and archives can be found at:

    http://sourceforge.net/mail/?group_id=9890

--------------------------
-- Known bugs           --
--------------------------

* On some containers, static pages (.html) may cause problems. A workaround is to
either rename these to pages that are dynamically processed (such as .shtml or
.jsp), or setup a servlet-mapping so dynamic servlets process these pages (i.e.
map .html to the SSIServlet).

* When working with charsets that aren't the default ones, issues may occur.
Consult mailing list for help.

* SiteMesh currently does not work flawlessly on Weblogic 6.1, 7.0 and 7.0 SP1. For more
information, visit http://jira.opensymphony.com/secure/ViewIssue.jspa?id=10045. However,
SiteMesh does work correctly on WebLogic 7.0 SP2, 8.1 and 8.1 SP2.

* SiteMesh does not work on Weblogic 8.1 SP1 because of various bugs in the JSP
compilation engine. Apply patch CR112789_81sp1 to your Weblogic installation.

* SiteMesh does not work on WebSphere 5.0.1 and 5.0.2. Apply patch IBM iFix 
PQ80592 to your WebSphere installation.

Please report bugs here: http://jira.opensymphony.com

--------------------------
-- Changes from 2.0.1   --
--------------------------

- Documentation updates.

JIRA bugs/tasks fixed:
    - SIM-73 PageFilter is not final and you can now subclass the newly protected applyDecorator and parsePage methods

--------------------------
-- Changes from 2.0     --
--------------------------

Chris Miller has been working like a demon on FastPageParser, and it's now as 
fast and efficient as it can get. To give you a rough idea, 2.0 is about 3 times 
faster than 1.5. 2.0.1 is about 5 times faster than 1.5.

With regards to memory usage, it's basically been knocked down to be negligible. 
Previously (1.5), a 50k page parsed 250 times (separate instances strongly 
referenced, with an explicit gc call to remove temporary objects) used up 37mb. 
Currently, it uses 25mb (and it's no coincidence that 50k * 250 * 2 bytes per 
char == 25mb).

- Minor DTD fix.

- Updates to documentation and build process.

- Added ParserGrinder to load test FastPageParser.

--------------------------
-- Changes from 1.5     --
--------------------------
- SiteMesh now hosted at http://sitemesh.dev.java.net.

- FastPageParser performance improvements.

- DTD location has changed; now http://www.opensymphony.com/sitemesh/dtd/sitemesh_1_5_decorators.dtd

- API change in Decorator (check your custom written Decorator classes):
  Added new method getRole() to enable role based decorators.

- Updated documentation to align with new Opensymphony Website.

JIRA bugs/tasks fixed:
    SIM-16 Tomcat IllegalStateException      
    SIM-41 NoSuchMethodException with Orion      
    SIM-2  Response bug on WebLogic 6.1      
    SIM-13 Tomcat4 throws IOException after response.sendRedirect()      
    SIM-29 WebLogic 7 doesn't work      
    SIM-40 Let properties be retrieved programatically      
    SIM-27 Example apps don't work in Pramati      
    SIM-17 Can't set headers from decorator page.      
    SIM-8  body tag not correctly parsed      
    SIM-56 Decorator taglibs allowed to contain body      
    SIM-37 role based decorators      
    SIM-46 Place TLDs in Jar file.      

--------------------------
-- Changes from 1.4.1   --
--------------------------
- API change in DecoratorMapper (check your custom written DecoratorMappers):
  before
        Decorator getNamedDecorator(String name);
  after
        Decorator getNamedDecorator(HttpServletRequest request, String name);

- API change in Decorator (check your custom written Decorator classes):
  Added new method getURIPath() to enable cross web-app support for decorators.

- New (shorter!) decorator xml format (backward compability is maintained),
  check above or the decorators.xml file in the /example/WEB-INF directory for an example.
  DTD: http://www.opensymphony.com/sitemesh/dtd/sitemesh_1_5_decorators.dtd
- Default SiteMesh configuration if sitemesh.xml is not present.
- When the request contains a Page object (under the key RequestConstants.PAGE)
  use this one (supports SiteMesh aware applications) so we don't need to parse.
- EnvEntryDecoratorMapper: allows the reference to a web-app environment entry for the
  decorator name, and falls back to ConfigDecoratorMapper's behavior if no matching
  environment entry is found.
- Cross web-app support for decorators by specifying <decorator ... webapp="anotherwebapp"/>.
  This will first try to get the decorator from anotherwebapp and fall back if not found.
- Small performance improvements in RobotDecoratorMapper.
- Some improvements to get SiteMesh working on different web containers.
  If you need to detect on which container your application is running,
  have a look at the com.opensymphony.module.sitemesh.util.Container class.

- BUGFIX: DefaultDecorator returned bad init param
- BUGFIX: small fixes to make SiteMesh work better on Tomcat
- BUGFIX: javadoc fixes

JIRA bugs/tasks fixed:
    SIM-1  Finalize RE support in PathMapper
    SIM-3  Page filter strips <xmp> tags
    SIM-4  PathMapper order is incorrect
    SIM-5  Add a mapper that uses environment entries
    SIM-6  When using the EnvEntryDecoratorMapper, decorations fail for html
    SIM-7  Parsing of body should strip doctype
    SIM-8  body tag not correctly parsed
    SIM-11 Parsing the text "<>" causes problems
    SIM-16 Define how charsets should work
    SIM-20 Cross web-app support for decorators
    SIM-21 FactoryException thrown when starting testsuite on WebLogic
    SIM-22 Create template web-app
    SIM-23 Distribution bundle
    SIM-32 A smaller than sign (<) in javascript fails

--------------------------
-- Changes from 1.4     --
--------------------------

- complete support for WebLogic 6.1 / 7
- complete support for Jetty 4
- internal optimizations to filter

- BUGFIX: StringIndexOutOfBoundsException in RobotDecoratorMapper

--------------------------
-- Changes from 1.3     --
--------------------------

- changed package structure from com.sitemesh to com.opensymphony.module.sitemesh
- many performance optimizations in FastPageParser
- various bug fixes and small performance improvements

- BUGFIX: memory leak in PageFilter
- BUGFIX: meta http-equiv tags are now added as properties with prefix meta.http-equiv.x where x 
  is the value of the http-equiv attribute (eg refresh)
- BUGFIX: the configuration files are not case-sensitive anymore


--------------------------
-- Credits              --
--------------------------

Thank these guys:
* Mathias Bogaert     <pathos@pandora.be>
* Mike Cannon-Brookes <mike@atlassian.com>
* Victor Salaman      <salaman@teknos.com>
* Joseph Ottinger     <joeo@adjacency.org>
* Hani Suleiman       <fate@users.sourceforge.net>
* Scott Farquhar      <scott@atlassian.com>

                                            - Joe Walnes <joe@truemesh.com>