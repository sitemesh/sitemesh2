<%@ taglib uri="http://www.opensymphony.com/sitemesh/decorator" prefix="decorator" %>
<html>
	<head>
		<title><decorator:title default="Mysterious page..." /></title>
		<decorator:head />
	</head>
	<body bgcolor="black" text="white" link="yellow" vlink="yellow" alink="yellow">

		<hr>
		<h1><decorator:title /></h1>
		<hr>

		<decorator:body />

	</body>
</html>
