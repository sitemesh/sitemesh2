<%@ taglib uri="http://www.opensymphony.com/sitemesh/decorator" prefix="decorator" %>

<html>
    <head>
        <title>My Site - <decorator:title default="Welcome!" /></title>
        <decorator:head />
    </head>

    <body>
        <decorator:body />
        <p><small>(<a href="?printable=true">printable version</a>)</small></p>
    </body>
</html>