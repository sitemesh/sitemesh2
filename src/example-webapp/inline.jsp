<%@ taglib uri="sitemesh-page" prefix="page" %>
<html>
	<head>
		<title>Inline decorator example</title>
	</head>
	<body>
		<p>This is a sample of an inline decorator.</p>

		<page:apply-decorator name="panel" page="index.html" />
		
		<page:apply-decorator name="panel">
			<page:param name="title">Inline content</page:param>
			Some inline stuff.
		</page:apply-decorator>

		<page:apply-decorator name="panel" title="More stuff">
			Some more inline stuff.
		</page:apply-decorator>
		
		<p>This is a sample of an inline decorator accessing external content.</p>
		
		<page:apply-decorator name="panel" page="http://www.opensymphony.com/" />

	</body>
</html>