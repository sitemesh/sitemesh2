<%! int count=0; %>
<html>
	<head>
		<title>JSP counter</title>
	</head>

	<body>
        <!-- THIS PAGE HAS A DELIBERATE ERROR IN IT! -->
		The current count is... <big><%= ++count %></big>
        <% int foo=((Sttring)request.getAttribute("foo")).length(); %>

	</body>
</html>
