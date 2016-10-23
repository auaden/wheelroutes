<%@ page import="com.app.domain.User" %>
<%--Created by IntelliJ IDEA.--%>
<%--User: adenau--%>
<%--Date: 8/7/16--%>
<%--Time: 12:11 AM--%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page isELIgnored="false" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <!-- The above 3 meta tags *must* come first in the head; any other head content must come *after* these tags -->
    <meta name="description" content="">
    <meta name="author" content="">
    <%--wheelroutes logo--%>
    <link rel="shortcut icon" href="images/favicon.ico" />
    <title>WheelRoutes Landing Page</title>
    <!-- Latest compiled and minified CSS -->
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css" integrity="sha384-BVYiiSIFeK1dGmJRAkycuHAHRg32OmUcww7on3RYdg4Va+PmSTsz/K68vbdEjh4u" crossorigin="anonymous">
    <link href="../../css/landing_page.css" type="text/css" rel="stylesheet" media="screen,projection"/>
    <!-- Link to font awesome for WheelRoutes Logo -->
    <script src="https://use.fontawesome.com/22342cf468.js"></script>
</head>

<body>

<!-- Fixed navbar -->
<nav role="navigation" class="navbar navbar-default navbar-fixed-top">
    <div class="container-fluid">
        <div class="row">
            <!-- Brand and toggle get grouped for better mobile display -->
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
                <ul class="nav navbar-nav navbar-right col-lg-3 col-md-5 col-sm-2">
                    <div class="btn-group" role="group" aria-label="...">
                        <button onclick="drop()" type="button" class="btn btn-default" id="login">Play
                        </button>
                    </div>
                </ul>
            </div> <!--End of row -->
        </div>
    </div>
</nav>

<div id="map" style="height: 95%;"></div>


<!-- jQuery (necessary for Bootstrap's JavaScript plugins) -->
<script src="https://ajax.googleapis.com/ajax/libs/jquery/1.12.4/jquery.min.js"></script>

<script>
    var map;
    //for drop marker function
    var markers = [];

    var defaultLat = 1.362361;
    var defaultLng = 103.814071;
    var defaultZoomLevel = 12;

    function initMap() {
        map = new google.maps.Map(document.getElementById('map'), {
            zoom: defaultZoomLevel,
            center: {lat: defaultLat, lng: defaultLng},
            // center: {lat: 40.740, lng: -74.18},
            scrollwheel: true,
            styles: [
                {
                    featureType: 'poi',
                    elementType: 'labels',
                    stylers: [
                        { visibility: 'off' }
                    ]
                }
            ]
        });

        var Colors = [
            "#000000",
            "#07cc04",
            "#99ff33",
            "#ccff33",
            "#ffff00",
            "#ff9933",
            "#ff751a",
            "#db2902"
        ];

        <c:forEach var="entry1" items="${viewCoordinates}">
        var rating = ${entry1.value} + 1
        var routeCoordinates = [
            <c:forEach var="mapEntry" items="${entry1.key}">
            //coordinate
            {lat:${mapEntry.latitude}, lng:${mapEntry.longitude}},
            </c:forEach>
        ];

        var route = new google.maps.Polyline({
            path: routeCoordinates,
            strokeOpacity: 1,
            strokeColor: Colors[rating],
            strokeWeight: 5
        });
        route.setMap(map);
        </c:forEach>
    }

    function drop() {
        <%--clearMarkers();--%>
        <%--var i = 0;--%>
        <%--<c:forEach var="entry" items="${viewCoordinates}">--%>
            <%--var position = {lat:${entry.latitude}, lng:${entry.longitude}}--%>
            <%--<c:if test="${entry.rating eq -1}">--%>
            <%--var color = "#000000";--%>
            <%--</c:if>--%>
            <%--<c:if test="${entry.rating eq 0}">--%>
            <%--var color = "#07cc04";--%>
            <%--</c:if>--%>
            <%--<c:if test="${entry.rating eq 1}">--%>
            <%--var color = "#99ff33";--%>
            <%--</c:if>--%>
            <%--<c:if test="${entry.rating eq 2}">--%>
            <%--var color = "#ccff33";--%>
            <%--</c:if>--%>
            <%--<c:if test="${entry.rating eq 3}">--%>
            <%--var color = "#ffff00";--%>
            <%--</c:if>--%>
            <%--<c:if test="${entry.rating eq 4}">--%>
            <%--var color = "#ff9933";--%>
            <%--</c:if>--%>
            <%--<c:if test="${entry.rating eq 5}">--%>
            <%--var color = "#ff751a";--%>
            <%--</c:if>--%>
            <%--<c:if test="${entry.rating eq 6}">--%>
            <%--var color = "#db2902";--%>
            <%--</c:if>--%>
            <%--addMarkerWithTimeout(position, i * 200, color, '${entry.timestamp}');--%>
            <%--i++;--%>
        <%--</c:forEach>--%>
    }

    function addMarkerWithTimeout(position, timeout, color, timestamp) {
        window.setTimeout(function() {
            markers.push(new google.maps.Marker({
                position: position,
                map: map,
                icon: {
                    path: google.maps.SymbolPath.CIRCLE,
                    fillOpacity: 1.0,
                    fillColor: color,
                    strokeColor: color,
                    scale: 3.0
                },
                title: timestamp
            }));
        }, timeout);
    }

    function clearMarkers() {
        for (var i = 0; i < markers.length; i++) {
            markers[i].setMap(null);
        }
        markers = [];
    }



</script>

<nav class="navbar navbar-default navbar-fixed-bottom">
    <div class="container">
        <i>Powered by <a class="orange-text text-lighten-3" href="https://wiki.smu.edu.sg/is480/IS480_Team_wiki%3A_2016T1_HumbleBees">Team HumbleBees</a></i>
    </div>
</nav>

<!-- Include all compiled plugins (below), or include individual files as needed -->
<script src="js/bootstrap.js"></script>
<script async defer
        src="https://maps.googleapis.com/maps/api/js?key=AIzaSyDgzAFdkkAz59bLW9T0ZyyFzjQbH6x3vxw&callback=initMap">
    // API key contained within
</script>
</body>
</html>