<%@ taglib uri="sitemesh-decorator" prefix="decorator" %>
<%@ taglib uri="sitemesh-page" prefix="page" %>

<html>
	<head>
		<title>{inline2} <decorator:title /></title>
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
					<page:applyDecorator name="inline-panel" id="inline1" title="Inline external content 1">
						<jsp:include page="panel1.jsp" />
					</page:applyDecorator>
				</td>
				<td width="100%" valign="top" id="bod">
					<decorator:body />
				</td>
				<td width="150" valign="top">
					<page:applyDecorator name="inline-panel" id="inline2">
						<jsp:include page="panel2.jsp" />
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