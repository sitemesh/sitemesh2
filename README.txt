*************************************
** OpenSymphony SiteMesh @VERSION@     **
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

* Orion 1.5.4 and up                         - http://www.orionserver.com
* Tomcat 4.0, 4.1 and 5.0.19                 - http://jakarta.apache.org/tomcat
* Resin 2.1.11, 2.1.12, 2.1.13 and 3.0.7     - http://www.caucho.com
* Oracle OC4J 2                              - http://www.oracle.com
* WebLogic 7.0 SP2, 8.1 and 8.1 SP2          - http://www.bea.com
* WebSphere 5.0                              - http://www.ibm.com
* Jetty 4.2.20                               - http://jetty.mortbay.org

--------------------------
-- Installation         --
--------------------------

* Copy sitemesh-@VERSION@.jar to the WEB-INF/lib/ directory of your web-app.

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

 * ORION USERS ONLY. For performance reasons, Orion does not auto-load tab library descriptors
   from Jars by default. To get passed this you will also have to copy sitemesh-decorator.tld
   and sitemesh-page.tld to WEB-INF/lib and add the following to WEB-INF/web.xml:

    <taglib>
        <taglib-uri>http://www.opensymphony.com/sitemesh/decorator</taglib-uri>
        <taglib-location>/WEB-INF/lib/sitemesh-decorator.tld</taglib-location>
    </taglib>

    <taglib>
        <taglib-uri>http://www.opensymphony.com/sitemesh/page</taglib-uri>
        <taglib-location>/WEB-INF/lib/sitemesh-page.tld</taglib-location>
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

    <%@ taglib uri="http://www.opensymphony.com/sitemesh/decorator" prefix="decorator" %>
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

* When working with charsets that aren't the default ones, issues may occur.
Consult mailing list for help.

* SiteMesh does not work on Weblogic 6.1, 7.0 and 7.0 SP1. For more
information, visit http://jira.opensymphony.com/secure/ViewIssue.jspa?id=10045. However,
SiteMesh does work correctly on WebLogic 7.0 SP2, 8.1 and 8.1 SP2.

* SiteMesh does not work on Weblogic 8.1 SP1 because of various bugs in the JSP
compilation engine. Apply patch CR112789_81sp1 to your Weblogic installation.

* SiteMesh does not work on WebSphere 5.0.1 and 5.0.2. Apply patch IBM iFix
PQ80592 to your WebSphere installation.

* SiteMesh's CookieDecoratorMapper does not work on Resin.

Please report bugs here: http://jira.opensymphony.com

--------------------------
-- Credits              --
--------------------------

Thank these guys:
* Mathias Bogaert         <NOSPAMpathos@pandora.be>
* Mike Cannon-Brookes     <mikeNOSPAM@atlassian.com>
* Victor Salaman          <salamanNOSPAM@teknos.com>
* Joseph Ottinger         <joeoNOSPAM@adjacency.org>
* Hani Suleiman           <NOSPAMfate@users.sourceforge.net>
* Scott Farquhar          <scottNOSPAM@atlassian.com>

                                            - Joe Walnes <joe@NOSPAMtruemesh.com>
