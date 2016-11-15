<%--
  Created by IntelliJ IDEA.
  User: adenau
  Date: 3/10/16
  Time: 10:06 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page isELIgnored="false" %>
<html>
<head>
    <title>Data Analytics</title>
</head>
<body>
<h1>COORD DATA</h1>
        <c:forEach var="entry1" items="${coordDataByIdAndTimestamp}">
            <table border="1" style="display: inline-block">

                <th>User Id</th>
                <th>Date</th>
                <th>Count</th>
                </tr>
            <c:forEach var="entry" items="${entry1.value}">
                <tr>
                    <td>${entry1.key}</td>
                    <td>${entry.key}</td>
                    <td>${entry.value}</td>
                </tr>
            </c:forEach>
            </table>
        </c:forEach>

<h1>AXIS DATA</h1>
        <c:forEach var="entry1" items="${axisDataByIdAndTimestamp}">
            <table border="1" style="display: inline-block">

                <th>User Id</th>
                <th>Date</th>
                <th>Count</th>
                </tr>
                <c:forEach var="entry" items="${entry1.value}">
                    <tr>
                        <td>${entry1.key}</td>
                        <td>${entry.key}</td>
                        <td>${entry.value}</td>
                    </tr>
                </c:forEach>
            </table>
        </c:forEach>




</body>
</html>
