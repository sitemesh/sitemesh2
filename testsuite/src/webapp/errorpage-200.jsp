<%@ page contentType="text/html;charset=UTF-8" language="java" isErrorPage="true" %><% response.setStatus(200); // as HttpWebUnit dies when you send a 500 error, then set to 200 just for testing!  %>
  <head><title>[-- An error has occurred --]</title></head>
  <body><%= exception %></body>
</html>