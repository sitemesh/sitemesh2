*** What is this? ***

This is the acceptance test suite for SiteMesh.

It consists of:

- a web application that demonstrates all aspects of SiteMesh.

- a set of unit tests built on top of JUnit and HttpUnit that check the correct
  output is being generated from the web-app.

- a configurable deployment system that shall automate deployment of the web-app
  to lots of different app-servers/servlet-engines.
  
- a test-runner that shall run the unit tests against the web-apps deployed on the various servers.
  
The idea is that a full unit test can be performed on SiteMesh for all servers in one step. 
Whenever any changes are made, the full test-suite should be re-run. When new features are added,
the test-suite should be modified and then SiteMesh modified until the tests pass again.


*** How do I use it? ***

There are two ways to use the testsuite:

 - Using the embedded web server (Jetty). This is designed for running the acceptance tests, QUICKLY without having
   to go through the hassle of tweaking external web-servers or configuration files. The JUnit TestSuite will start
   Jetty automatically, without relying on another process.

 - Using external web servers. This is designed to check that the acceptance tests pass on all different platforms.
   This is a bit more painful as the app-servers have to be configured and the web-app has to be deployed to each of
   them.


*** Using the embedded server ***

 1) Build the web-app with 'ant webapp-build'.
 2) In your JUnit test-runner (recommend your IDE), run the test suite class
     testsuite.sitemesh.SiteMeshTestSuite$OnEmbeddedServer, ensuring that the working directory is the same that this
     file lives in.

Note: The first run can take some time, but subsequent runs are very quick.


*** Using external web servers ***

Initial setup:
 1) Install all the app-servers/servlet-engines you wish to test with. The servers don't need to be
    on the same machine - so long as you can access the directory the web-apps are to be deployed to, they can
    be on other machines on the network.
 2) Build the web-app with 'ant webapp-build'.
 3) Deploy the resulting .war file in the dist directory to each server.
 4) Fire up all the servers (and leave them running). Make sure they are not all trying to listen on the same port :)
 5) Edit tests.xml. For each server, an entry should be made saying where the web-app is deployed to on the filesystem
    (the app can be a .war, .ear or unpacked directory) and what the URL to access it is.

Now each time a modification is made to SiteMesh or the test-suite a two step process must be performed:
 1) Run 'ant deploy' to rebuild the web-app and redeploy it to each server.
 2) Run 'ant test' to run the testsuite against the new server.

Note: It is important that the deployment process has completed on all servers before running the second step - this
takes longer on some servers.



-Joe.