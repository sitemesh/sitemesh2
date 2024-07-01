<%@ taglib uri="http://www.opensymphony.com/sitemesh/decorator" prefix="decorator" %>
<html>
	<head>
		<title><decorator:title  /></title>
		<decorator:head />
	</head>
	<body>

		<h1><decorator:title /></h1>
		<p align="right"><i>(printable version)</i></p>

		<decorator:body />

	</body>
</html>
