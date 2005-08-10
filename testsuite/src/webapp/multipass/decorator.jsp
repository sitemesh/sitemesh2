<%@ taglib uri="http://www.opensymphony.com/sitemesh/decorator" prefix="decorator" %>
<html>
    <head>
        <title><decorator:title/></title>
    </head>
    <body>
        <!-- Decorator -->
        {<decorator:extractProperty property="div.c"/>}
        <hr/>
        <decorator:body/>
        <hr/>
        {<decorator:extractProperty property="div.a"/>}
        {<decorator:getProperty property="div.b"/>}
	</body>
</html>
