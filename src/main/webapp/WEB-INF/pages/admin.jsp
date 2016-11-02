<%--
  Created by IntelliJ IDEA.
  User: adenau
  Date: 20/7/16
  Time: 12:56 AM
  To change this template use File | Settings | File Templates.
--%>
<!DOCTYPE html>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page isELIgnored="false" %>
<html>
<head>
    <title>Admin Page</title>
    <meta name="viewport" content="initial-scale=1.0">
    <meta charset="utf-8">
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css" integrity="sha384-BVYiiSIFeK1dGmJRAkycuHAHRg32OmUcww7on3RYdg4Va+PmSTsz/K68vbdEjh4u" crossorigin="anonymous">
</head>
<body>
    <h1>FILTER: </h1>
    <a class="btn btn-default" href="/routesView.do">View by routes</a> <br>
    <a class="btn btn-default" href="/coordinatesView.do">View by coordinates (playable)</a>
    <a class="btn btn-default" href="/process-data.do">Process Data</a>

    <table border="1">
        <tr>
            <th>Email</th>
            <th>Timestamp</th>
            <th>Obstacle</th>
            <th>Description</th>
            <th>Approved?</th>
            <th>Action</th>
        </tr>

        <c:forEach var="entry" items="${unapprovedObstacles}">
        <tr>
                <td>${entry.email}</td>
                <td>${entry.timestamp}</td>
                <td><img src="http://wheelroutes.icitylab.com/rest/obstacle/${entry.latitude}/${entry.longitude}/" style="width:200px; height:200px"></td>
                <td>${entry.description}</td>
                <td>${entry.approved}</td>
                <td>
                    <form action="/process-approve-obstacle.do" method="post">
                        <input type="hidden" name="email" value="${entry.email}">
                        <input type="hidden" name="lat" value="${entry.latitude}">
                        <input type="hidden" name="lng" value="${entry.longitude}">
                        <input type="submit" value="APPROVE" >
                    </form>
                </td>
        </tr>
        </c:forEach>

    </table>



</body>
</html>
