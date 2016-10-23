<%--
  Created by IntelliJ IDEA.
  User: adenau
  Date: 14/7/16
  Time: 10:50 PM
  To change this template use File | Settings | File Templates.
--%>

<%
    session.invalidate();
    response.sendRedirect("landing.do");
%>