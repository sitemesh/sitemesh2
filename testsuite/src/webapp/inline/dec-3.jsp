<%@ taglib uri="http://www.opensymphony.com/sitemesh/decorator" prefix="decorator" %>
<%@ taglib uri="http://www.opensymphony.com/sitemesh/page" prefix="page" %>

<html>
	<head>
		<title>{inline3} <decorator:title /></title>
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
					<page:applyDecorator page="panel1.jsp" name="inline-panel" id="inline1" title="Inline external content 1" />
				</td>
				<td width="100%" valign="top" id="bod">
					<decorator:body />
				</td>
				<td width="150" valign="top">
					<page:applyDecorator page="/inline/panel2.jsp" name="inline-panel" id="inline2" />
					<br>
					<page:applyDecorator page="../inline/panel1.jsp" name="inline-panel" id="inline3" title="Inline external content 3" />
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