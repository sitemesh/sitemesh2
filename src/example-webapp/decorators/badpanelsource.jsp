<%@ taglib uri="http://www.opensymphony.com/sitemesh/decorator" prefix="decorator" %>
<%@ taglib uri="http://www.opensymphony.com/sitemesh/page" prefix="page" %>
<html>
	<head>
		<title><decorator:title default="Mysterious page..." /></title>
		<link rel=stylesheet href="<%= request.getContextPath() %>/decorators/main.css">
		<decorator:head />
	</head>
	<body>
        <p>You shouldn't see this page.
		<table height="100%" width="100%">
			<tr>
				<td valign=top>
					<page:applyDecorator page="/tiny-panel.html" name="panel" />
					<page:applyDecorator page="/counter-syntaxerror.jsp" name="panel" />
					<page:applyDecorator page="/google.html" name="panel" />
					<%--page:applyDecorator page="/random.pl" name="panel" /--%>
				</td>
				<td width="100%">
					<table width="100%" height="100%">
						<tr>
							<td id="pageTitle">
								<decorator:title />
							</td>
						</tr>
						<tr>
							<td valign="top" height="100%">
								<decorator:body />
							</td>
						</tr>
						<tr>
							<td id="footer">
								<b>Disclaimer:</b>
								This site is an example site to demonstrate SiteMesh. It serves no other purpose. Sue us.
							</td>
						</tr>
					</table>
				</td>
			</tr>
		</table>

		<%-- Construct URL from page request and append 'printable=true' param --%>
		<decorator:usePage id="p" />
		<%
			StringBuffer printUrl = new StringBuffer();
			printUrl.append(request.getRequestURI());
			printUrl.append("?printable=true");
			if (request.getQueryString()!=null) {
				printUrl.append('&');
				printUrl.append(request.getQueryString());
			}
		%>
		<p align="right">[ <a href="<%= printUrl %>">printable version</a> ]</p>

	</body>
</html>
