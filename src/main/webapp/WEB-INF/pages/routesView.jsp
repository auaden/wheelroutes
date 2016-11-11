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
    <%--stylesheet for filter slider bar --%>
    <link href="../../css/iThing-min.css" type="text/css" rel="stylesheet" media="screen,projection"/>
    <link rel="stylesheet" href="../../css/jquery-ui.min.css" type="text/css" />
    <link rel="stylesheet" href="../../css/jquery-ui.structure.min.css" type="text/css" />
    <link rel="stylesheet" href="../../css/jquery-ui.theme.min.css" type="text/css" />
    <!-- Link to font awesome for WheelRoutes Logo -->
    <script src="https://use.fontawesome.com/22342cf468.js"></script>
</head>

<body>
<%--hidden form field for processing of filter inputs--%>
<div class="filterResults">
    <form id="filterForm" action="/process-filter-routes.do"  method="POST"> 
        <label class="control-label">User ID: </label>
        <input class="form-control" type="hidden" id="userId" name="userId" value=""><br> 
        <label class="control-label">Start Date and Time</label>
        <input class="form-control" type="hidden" id="startDate" name="startDate" value=""><br> 
        <label class="control-label">End Date and Time</label>
        <input class="form-control" type="hidden" id="endDate" name="endDate" value=""><br> 

        <input class="btn btn-default" type="submit" value="submit"> 
    </form>
</div>

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
                <ul class="nav navbar-nav navbar-right col-lg-2 col-md-5 col-sm-2">
                    <a href="/admin.do"><button class="btn btn-default" type="button" id="back">Back</button></a>
                </ul>
            </div>
                <%--<ul class="nav navbar-nav navbar-right col-lg-3 col-md-5 col-sm-2">--%>
                    <%--<div class="btn-group" role="group" aria-label="...">--%>
                        <%--<button onclick="drop()" type="button" class="btn btn-default" id="login">Display--%>
                        <%--</button>--%>
                    <%--</div>--%>
                <%--</ul>--%>
            <%--</div> <!--End of row -->--%>
        </div>
    </div>
</nav>

<div id="map" style="height:95%"></div>

<div class="navbar navbar-default navbar-fixed-bottom">
    <div class="container-fluid">
        <div class="row">
            <%--<div class="navbar-header col-lg-2 col-md-2 col-sm-2">--%>
            <%--<br/><center><i>Powered by <br/><a class="orange-text text-lighten-3" href="https://wiki.smu.edu.sg/is480/IS480_Team_wiki%3A_2016T1_HumbleBees">Team HumbleBees</a></i></center>--%>
            <%--</div>--%>
            <form id="filterForm" action="/process-filter.do"  method="POST"> 
                <div class="dropup col-lg-1 col-md-2 col-sm-1">
                    <button class="btn btn-primary dropdown-toggle" type="button" id="userIdList" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                        UserID
                        <span class="caret"></span>
                    </button>
                    <ul class="dropdown-menu dropdown-menu-left" aria-labelledby="userIdList" id="userIdList" style="cursor:pointer;">
                        <li class="dropdown-header"><center>Filter by User ID</center></li>
                        <li><center>2</center></li>
                        <li role="separator" class="divider"></li>
                        <li><center>3</center></li>
                        <li role="separator" class="divider"></li>
                        <li><center>4</center></li>
                        <li role="separator" class="divider"></li>
                        <li><center>5</center></li>
                        <li role="separator" class="divider"></li>
                        <li><center>6</center></li>
                        <li role="separator" class="divider"></li>
                        <li><center>7</center></li>
                        <li role="separator" class="divider"></li>
                        <li><center>8</center></li>
                        <li role="separator" class="divider"></li>
                        <li><center>9</center></li>
                        <li role="separator" class="divider"></li>
                        <li><center>10</center></li>
                        <li role="separator" class="divider"></li>
                        <li><center>11</center></li>
                        <li role="separator" class="divider"></li>
                        <li><center>99</center></li>
                    </ul>
                </div>

                <div class="col-lg-5 col-md-4 col-sm-5">
                    <div id="dateSlider"></div>
                </div>

                <div class="col-lg-5 col-md-4 col-sm-5">
                    <div id="hourSlider"></div>
                </div>

                <div class="col-lg-1 col-md-2 col-sm-1">
                    <button type="button" id="filter" class="btn btn-info" onclick="filterResults()">Filter</button>
                </div>
            </form>
        </div>
    </div>
</div>

<!-- jQuery (necessary for Bootstrap's JavaScript plugins) -->
<script src="https://ajax.googleapis.com/ajax/libs/jquery/1.12.4/jquery.min.js"></script>
<script src="../../js/jquery-ui.min.js"></script>
<script src="../../js/jQRangeSlider-min.js"></script>
<script src="../../js/jQDateRangeSlider-min.js"></script>

<script>
    var map;
    //for drop marker function
    var markers = [];
    var defaultLat = 1.362361;
    var defaultLng = 103.814071;
    var defaultZoomLevel = 12;

    function TwoDigits(val){
        if (val < 10){
            return "0" + val;
        }

        return val;
    }

    //to retrieve filter function chose User ID
    $('#userIdList li center').on('click', function(){
        userId = +this.innerHTML;
        $("#userIdList").text(userId);
        console.log("Value is " + userId);
    });

    function filterResults(){
        var dateMin = $("#dateSlider").dateRangeSlider("min");
        var dateMax = $("#dateSlider").dateRangeSlider("max");

        //to get 2 digit format month for slice method
        var monthMin = ("0" + (dateMin.getMonth()+1)).slice(-2);
        var monthMax = ("0" + (dateMax.getMonth()+1)).slice(-2);

        var dayMin = ("0" + (dateMin.getDate())).slice(-2);
        var dayMax = ("0" + (dateMax.getDate())).slice(-2);

        var dateFormatMin = dateMin.getFullYear() + "-" + monthMin + "-" +  dayMin;
        var dateFormatMax = dateMax.getFullYear() + "-" + monthMax + "-" + dayMax;

        var hourMin = $("#hourSlider").dateRangeSlider("min");
        var hourMax = $("#hourSlider").dateRangeSlider("max");

        var hourFormatMin = ("0" + hourMin.getHours()).slice(-2) + ":"  + ("0" + hourMin.getMinutes()).slice(-2);
        var hourFormatMax = ("0" + hourMax.getHours()).slice(-2) + ":"  + ("0" + hourMax.getMinutes()).slice(-2);

        document.getElementById("userId").value = userId;
        document.getElementById("startDate").value = dateFormatMin + " " + hourFormatMin;
        document.getElementById("endDate").value = dateFormatMax + " " + hourFormatMax;

        console.log("UserID: " + userId + ", Date Min:" + dateFormatMin + ", Date Max: " + dateFormatMax + ", Hour Min: " + hourFormatMin + ", Hour Max: " + hourFormatMax);

        document.getElementById("filterForm").submit();
    }

    <c:if test="${dateMap eq null}">
    $("#hourSlider").dateRangeSlider({
        //FORMAT
        //Date(year, month, day, hours, minutes, seconds, milliseconds)
        bounds: {min: new Date(2016, 0, 1), max: new Date(2016, 0, 1, 23, 59, 59)},
        defaultValues: {min: new Date(2016, 0, 1, 8), max: new Date(2016, 0, 1, 18)},
        formatter: function(value){
            var hours = value.getHours(),
                    minutes = value.getMinutes();
            return TwoDigits(hours) + ":" + TwoDigits(minutes);
        }
    });
    </c:if>

    <c:if test="${dateMap ne null}">
    $("#hourSlider").dateRangeSlider({
        //FORMAT
        //Date(year, month, day, hours, minutes, seconds, milliseconds)
        bounds: {
            min: new Date(2016, 0, 1),
            max: new Date(2016, 0, 1, 23, 59, 59)
        },
        defaultValues: {
            min: new Date(2016, 0, 1, ${dateMap['startHour']}, ${dateMap['startMinute']}),
            max: new Date(2016, 0, 1, ${dateMap['endHour']}, ${dateMap['endMinute']})
        },
        formatter: function(value){
            var hours = value.getHours(),
                    minutes = value.getMinutes();
            return TwoDigits(hours) + ":" + TwoDigits(minutes);
        }
    });
    </c:if>

    var oneWeekAgo = new Date();
    oneWeekAgo.setDate(oneWeekAgo.getDate() - 7);

    <c:if test="${dateMap eq null}">
    $("#dateSlider").dateRangeSlider(
            {defaultValues:{
                min: oneWeekAgo,
                max: new Date()
            }},
            {bounds:{
                min: new Date(2016, 8, 18),
                max: new Date()
            }}
    );
    </c:if>

    <c:if test="${dateMap ne null}">
    $("#dateSlider").dateRangeSlider(
            {defaultValues:{
                min: new Date(${dateMap['startYear']}, ${dateMap['startMonth']} - 1, ${dateMap['startDay']}),
                max: new Date(${dateMap['endYear']}, ${dateMap['endMonth']} - 1, ${dateMap['endDay']})
            }},
            {bounds:{
                min: new Date(2016, 8, 18),
                max: new Date()
            }}
    );
    </c:if>

    function initMap() {
        map = new google.maps.Map(document.getElementById('map'), {
            zoom: defaultZoomLevel,
            center: {lat: defaultLat, lng: defaultLng},
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
        var rating = ${entry1.value.rating} + 1
        var routeCoordinates = [
            <c:forEach var="mapEntry" items="${entry1.value.route}">
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


</script>

<!-- Include all compiled plugins (below), or include individual files as needed -->
<script src="js/bootstrap.js"></script>
<script async defer
        src="https://maps.googleapis.com/maps/api/js?key=AIzaSyDgzAFdkkAz59bLW9T0ZyyFzjQbH6x3vxw&callback=initMap">
    // API key contained within
</script>
</body>
</html>