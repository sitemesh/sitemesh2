<%@ taglib uri="http://www.opensymphony.com/sitemesh/decorator" prefix="decorator" %>
<%@ taglib uri="http://www.opensymphony.com/sitemesh/page" prefix="page" %>
<%@ include file="function.jsp"%>

<decorator:usePage id="p" />

decorator-main.jsp, request: <%= dumpRequest(request) %>

decorator-main.jsp, page.request before applyDecorator: <%= dumpRequest(p.getRequest()) %>

<decorator:body />
<page:applyDecorator page="/request/inline.jsp" name="/request/decorator-inline.jsp" />

decorator-main.jsp, page.request after applyDecorator: <%= dumpRequest(p.getRequest()) %>
