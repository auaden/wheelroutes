<%@ page import="com.app.domain.User" %><%--
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
    <%--<title>Admin Page</title>--%>
    <%--<meta name="viewport" content="initial-scale=1.0">--%>
    <%--<meta charset="utf-8">--%>
    <%--<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css" integrity="sha384-BVYiiSIFeK1dGmJRAkycuHAHRg32OmUcww7on3RYdg4Va+PmSTsz/K68vbdEjh4u" crossorigin="anonymous">--%>

    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <!-- The above 3 meta tags *must* come first in the head; any other head content must come *after* these tags -->
    <meta name="description" content="">
    <meta name="author" content="">

    <%--wheelroutes logo--%>
    <link rel="shortcut icon" href="images/favicon.ico" />
    <title>WheelRoutes Admin Page</title>

    <!-- Latest compiled and minified CSS -->
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css" integrity="sha384-BVYiiSIFeK1dGmJRAkycuHAHRg32OmUcww7on3RYdg4Va+PmSTsz/K68vbdEjh4u" crossorigin="anonymous">
    <link href="../../css/landing_page.css" type="text/css" rel="stylesheet" media="screen,projection"/>
    <link href="../../css/jumbotron-narrow.css" type="text/css" rel="stylesheet" media="screen,projection"/>
    <!-- Link to font awesome for WheelRoutes Logo -->
    <script src="https://use.fontawesome.com/22342cf468.js"></script>
</head>
<body>
    <%--<h1>FILTER: </h1>--%>
    <%--<a class="btn btn-default" href="/routesView.do">View by routes</a> <br>--%>
    <%--<a class="btn btn-default" href="/coordinatesView.do">View by coordinates (playable)</a>--%>
    <%--<a class="btn btn-default" href="/process-data.do">Process Data</a>--%>
    <%
        if (request.getAttribute("authUser") == null) {
            response.sendRedirect("/landing.do");
        } else {
            User user = (User) request.getAttribute("authUser");
            if (!user.getEmail().contains("humblebees")) {
                response.sendRedirect("/landing.do");
            }
        }
    %>
    <nav role="navigation" class="navbar navbar-default navbar-fixed-top">
        <div class="container-fluid">
            <!-- <div class="header clearfix"> -->
            <div class="row">
                <div class="navbar-header col-lg-2 col-md-2 col-sm-2">
                    <button type="button" data-target="#navbarCollapse" data-toggle="collapse" class="navbar-toggle">
                        <span class="sr-only">Toggle navigation</span>
                        <span class="icon-bar"></span>
                        <span class="icon-bar"></span>
                        <span class="icon-bar"></span>
                    </button>
                    <a class="navbar-brand" href="#"><i class="fa fa-wheelchair fa-2x" aria-hidden="true"></i>WheelRoutes</a>
                </div>
                <div id="collapse navbar-collapse" class="collapse navbar-collapse">
                    <ul class="nav navbar-nav navbar-right col-lg-2 col-md-5 col-sm-2">
                        <a href="/landing.do"><button class="btn btn-default" type="button" id="back">Back</button></a>
                    </ul>
                </div>
            </div>
        </div>
    </nav>

    <div class="jumbotron">
        <h1>Admin Page</h1>
        <p class="lead">Process | View | Approve</p>
        <%--<a class="btn btn-lg btn-warning" href="/process-data.do" role="button">Process Data</a>--%>
        <a class="btn btn-default" href="/process-data.do" role="button" style="background-color:#3D5B6E; color:white">Process Data</a>
        <a class="btn btn-default" href="/data-analytics.do" role="button" style="background-color:#3D5B6E; color: white">Data Analytics</a>
        <a class="btn btn-default" href="/routesView.do" role="button" style="background-color:#177E89; color:white">View by Routes</a>
        <a class="btn btn-default" href="/coordinatesView.do" role="button" style="background-color:#177E89; color:white">View by Coordinates</a>
    </div>

    <div class="container-fluid">
        <div class="row marketing">
            <div class="col-sm-offset-1 col-lg-10">
                <h4>List of Obstacles</h4>
                <%--<p>Donec id elit non mi porta gravida at eget metus. Maecenas faucibus mollis interdum.</p>--%>
                <div class="table-responsive">
                    <table class="table table-responsive table-bordered table-striped table-hover table-condensed">
                        <tr style="background-color:#444345; color: white">
                            <th>No.</th>
                            <th>Email</th>
                            <th>Timestamp</th>
                            <th>Obstacle</th>
                            <th>Description</th>
                            <th>Approved?</th>
                            <th>Action</th>
                        </tr>
                         <c:forEach var="entry" items="${unapprovedObstacles}" varStatus="loop">
                            <tr>
                                <td>${loop.count}</td>
                                <td>${entry.email}</td>
                                <td>${entry.timestamp}</td>
                                <%--<td><img src="http://wheelroutes.icitylab.com/rest/obstacle/${entry.latitude}/${entry.longitude}/" style="width:200px; height:200px"></td>--%>
                                <td><img src="http://localhost:8080/rest/obstacle/${entry.latitude}/${entry.longitude}/" style="width:200px; height:200px"></td>
                                <td>${entry.description}</td>
                                <td>${entry.approved}</td>
                                <td>
                                    <div class="container-fluid">
                                        <div class="row">
                                        <form action="/process-approve-obstacle.do" method="post">
                                            <input type="hidden" name="email" value="${entry.email}">
                                            <input type="hidden" name="lat" value="${entry.latitude}">
                                            <input type="hidden" name="lng" value="${entry.longitude}">
                                            <button type="submit" class="btn btn-success btn-block">Approve</button></center>
                                            <%--<input type="submit" value="APPROVE" >--%>
                                        </form>
                                        </br>
                                        <form action="/process-delete-obstacle.do" method="post">
                                            <input type="hidden" name="email" value="${entry.email}">
                                            <input type="hidden" name="lat" value="${entry.latitude}">
                                            <input type="hidden" name="lng" value="${entry.longitude}">
                                            <button type="submit" class="btn btn-danger btn-block">Delete</button></center>
                                        </form>
                                        </div>
                                    </div>
                                </td>
                            </tr>
                        </c:forEach>
                    </table>
                </div>
            </div>
        </div>
    </div>

    <%--<table border="1">--%>
        <%--<tr>--%>
            <%--<th>Email</th>--%>
            <%--<th>Timestamp</th>--%>
            <%--<th>Obstacle</th>--%>
            <%--<th>Description</th>--%>
            <%--<th>Approved?</th>--%>
            <%--<th>Action</th>--%>
        <%--</tr>--%>

        <%--<c:forEach var="entry" items="${unapprovedObstacles}">--%>
        <%--<tr>--%>
                <%--<td>${entry.email}</td>--%>
                <%--<td>${entry.timestamp}</td>--%>
                <%--<td><img src="http://wheelroutes.icitylab.com/rest/obstacle/${entry.latitude}/${entry.longitude}/" style="width:200px; height:200px"></td>--%>
                <%--<td>${entry.description}</td>--%>
                <%--<td>${entry.approved}</td>--%>
                <%--<td>--%>
                    <%--<form action="/process-approve-obstacle.do" method="post">--%>
                        <%--<input type="hidden" name="email" value="${entry.email}">--%>
                        <%--<input type="hidden" name="lat" value="${entry.latitude}">--%>
                        <%--<input type="hidden" name="lng" value="${entry.longitude}">--%>
                        <%--<input type="submit" value="APPROVE" >--%>
                    <%--</form>--%>
                <%--</td>--%>
        <%--</tr>--%>
        <%--</c:forEach>--%>

    <%--</table>--%>

    <div class="navbar navbar-default navbar-fixed-bottom">
        <div class="container-fluid">
            <div class="row">
                <div class="navbar-header col-lg-4 col-md-2 col-sm-2">
                    <center><i>Powered by <a class="orange-text text-lighten-3" href="https://wiki.smu.edu.sg/is480/IS480_Team_wiki%3A_2016T1_HumbleBees">Team HumbleBees</a></i></center>
                </div>
            </div>
        </div>
    </div>
</body>
</html>
