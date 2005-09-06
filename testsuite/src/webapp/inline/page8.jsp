<%@ taglib prefix="page" uri="http://www.opensymphony.com/sitemesh/page" %>
<%@ taglib prefix="test" uri="test-tags" %>

<html>
    <head>
		<title>content 8</title>
	</head>
	<body>

        <page:applyDecorator name="inline-panel">
<!-- this test is here to prove that param tags should not just search their parents, but ancestry tags as well: SIM-179 --> 
            <test:testTag >
                <page:param name="title">Inline Title</page:param>
            </test:testTag>
            Some inline stuff.
        </page:applyDecorator>


	</body>
</html>
