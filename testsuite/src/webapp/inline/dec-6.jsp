<%@ taglib uri="http://www.opensymphony.com/sitemesh/decorator" prefix="decorator" %>
<%@ taglib uri="http://www.opensymphony.com/sitemesh/page" prefix="page" %>

<html>
	<head>
		<title>{inline6} <decorator:title /></title>
		<decorator:head />
	</head>
	<body>

    <h1><decorator:title /></h1>

    <table border><tr><td id="bod"><decorator:body /></td></tr></table>

    <h2 id="footer">footer</h2>

	</body>
</html>