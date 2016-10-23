<%--
  Created by IntelliJ IDEA.
  User: adenau
  Date: 11/10/16
  Time: 1:49 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page isELIgnored="false" %>
<html>
<head>
    <title>Database</title>
</head>
<body>


    <table border="1">
        <tr>
            <th>User Id</th>
            <th>Timestamp</th>
            <th>Lat</th>
            <th>Lng</th>
            <th>NumSat</th>
        </tr>
        <c:forEach var="dataEntry" items="${data}">
            <tr>
                <td>${dataEntry.userId}</td>
                <td>${dataEntry.timestamp}</td>
                <td>${dataEntry.latitude}</td>
                <td>${dataEntry.longitude}</td>
                <td>${dataEntry.numSat}</td>
            </tr>
        </c:forEach>
    </table>

    <table border="1">
        <tr>
            <th>User Id</th>
            <th>Timestamp</th>
            <th>x</th>
            <th>y</th>
            <th>z</th>
        </tr>
        <c:forEach var="dataEntry" items="${axisData}">
            <tr>
                <td>${dataEntry.userId}</td>
                <td>${dataEntry.timestamp}</td>
                <td>${dataEntry.xAxis}</td>
                <td>${dataEntry.yAxis}</td>
                <td>${dataEntry.zAxis}</td>
            </tr>
        </c:forEach>
    </table>
</body>
</html>
