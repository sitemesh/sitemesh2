<%@ taglib uri="http://www.opensymphony.com/sitemesh/decorator" prefix="decorator" %>
<html>
	<head>
		<title>[:: <decorator:title default="MySite" /> ::]</title>
		<style>
			div, p, td { font-family: arial; }
			#header, #footer { text-align: center; color: white; background-color: black; }
			#header { font-size: 20pt; }
			#mainbody { font-size: 9pt; }
		</style>
		<decorator:head />
	</head>
	<body>

		<div id="header"><decorator:title default="MySite" /></div>

		<div id="mainbody"><decorator:body /></div>

		<div id="footer">footer</div>

	</body>
</html>