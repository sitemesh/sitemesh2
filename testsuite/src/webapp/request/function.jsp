<%!
  String dumpRequest(HttpServletRequest req) {
    return req.getRequestURI() + "|" + req.getQueryString();
  }
%>
