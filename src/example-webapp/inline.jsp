<%@ taglib uri="http://www.opensymphony.com/sitemesh/page" prefix="page" %>
<html>
	<head>
		<title>Inline decorator example</title>
	</head>
	<body>
		<p>This is a sample of an inline decorator.</p>

		<page:applyDecorator name="panel" page="index.html" />

		<page:applyDecorator name="panel">
			<page:param name="title">Inline content</page:param>
			Some inline stuff.
		</page:applyDecorator>

		<page:applyDecorator name="panel" title="More stuff">
			Some more inline stuff.
		</page:applyDecorator>

		<p>This is a sample of an inline decorator accessing external content.</p>

		<page:applyDecorator name="panel" page="http://www.opensymphony.com/" />

	</body>
</html>