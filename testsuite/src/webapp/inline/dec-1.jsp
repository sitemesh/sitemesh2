<%@ taglib uri="http://www.opensymphony.com/sitemesh/decorator" prefix="decorator" %>
<%@ taglib uri="http://www.opensymphony.com/sitemesh/page" prefix="page" %>

<html>
	<head>
		<title>{inline1} <decorator:title /></title>
		<decorator:head />
	</head>
	<body>

		<table border="1" width="100%" height="100%">
			<tr>
				<td colspan="3" align="center">
					<font size="5"><b><decorator:title /></b></font>
				</td>
			</tr>
			<tr>
				<td width="150" valign="top">
					<page:applyDecorator name="inline-panel" id="inline1">
						<title>Inline internal content 1</title>
						<p>Something inside something one</p>
						<center><b>:-)<br><br>:-D</b></center>
					</page:applyDecorator>
				</td>
				<td width="100%" valign="top" id="bod">
					<decorator:body />
				</td>
				<td width="150" valign="top">
					<page:applyDecorator name="inline-panel" id="inline2" title="Inline internal content 2">
						<p>Something inside something two</p>
						<center><b>:-)<br><br>:-D</b></center>
					</page:applyDecorator>
				</td>
			</tr>
			<tr>
				<td id="footer" colspan=3>
					footer
				</td>
			</tr>
		</table>

	</body>
</html>