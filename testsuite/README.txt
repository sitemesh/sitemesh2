*** What is this? ***

This is a testsuite for SiteMesh. 

It consists of:

- a web application that demonstrates all aspects of SiteMesh.

- a set of unit tests built on top of JUnit and HttpUnit that check the correct
  output is being generated from the web-app.

- a configurable deployment system that shall automate deployment of the web-app
  to lots of different app-servers/servlet-engines.
  
- a configurable test-runner that shall run the unit tests against the web-apps 
  deployed on the various servers.
  
The idea is that a full unit test can be performed on SiteMesh for all servers in one step. 
Whenever any changes are made, the full test-suite should be re-run. When new features are added,
the test-suite should be modified and then SiteMesh modified until the tests pass again.

*** How do I use it? ***

First, install all the app-servers/servlet-engines you wish to test with. The servers don't need to be 
on the same machine - so long as you can access the directory the web-apps are to be deployed to, they can
be on other machines on the network.

Build the web-app with 'ant webapp-build'. Deploy the resulting .war file in the dist directory to each server.
Fire up all the servers (and leave them running). Make sure they are not all trying to listen on the same port :)

Edit tests.xml. For each server, an entry should be made saying where the web-app is deployed to on the filesystem 
(the app can be a .war, .ear or unpacked directory) and what the URL to access it is.

Now each time a modification is made to SiteMesh or the test-suite a two step process must be performed:

1) Run 'ant deploy' to rebuild the web-app and redeploy it to each server.
2) Run 'ant test' to run the testsuite against the new server.

It is important that the deployment process has completed on all servers before running the second step - this takes 
longer on some servers. 

-Joe.