<%@ taglib uri="http://www.opensymphony.com/sitemesh/decorator" prefix="decorator" %>

<table width="200" border="1" cellspacing="0">
    <tr>
        <td bgcolor="darkblue" align="center">
            <font color="white" face="arial"><b>
                <span id="<decorator:getProperty property="id" default="unknown" />"><decorator:title /></span>
            </b></font>
        </td>
    </tr>
    <tr>
        <td bgcolor="lightgrey">
            <font size="1" face="arial" id="inline-content">
                <decorator:body />
            </font>
        </td>
    </tr>
</table>
<br>