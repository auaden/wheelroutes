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

<%--<body onload="checkCookie()">--%>
<body>
    <%--Modal for login function--%>
    <div class="modal fade" id="exampleModal" tabindex="-1" role="dialog" aria-labelledby="exampleModalLabel">
        <div class="modal-dialog" role="document">
            <div class="modal-content">
                <div class="modal-header">
                    <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
                    <h4 class="modal-title" id="exampleModalLabel"><center>Login Details</center></h4>
                </div>
                <form:form modelAttribute="user" action="process-login.do" class="form-signin">
                    <div class="modal-body">
                        <div class="form-group">
                            <label for="email" class="control-label">Username</label>
                            <form:input path="email" type="email" class="form-control validate" placeholder="Email Address"/>
                            <!--<input type="email" class="form-control" id="username">-->
                        </div>
                        <div class="form-group">
                            <label for="password" class="control-label">Password</label>
                            <form:input path="password" type="password" class="form-control validate" placeholder="Password"/>
                            <!--<input type="password" class="form-control" id="password">-->
                        </div>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-secondary" data-dismiss="modal" onclick="Redirect()">Not Registered?</button>
                        <!--<a href="/register.do"><button class="btn center">Not registered?</button></a>-->
                        <input type="submit" class="btn btn-primary" value="Sign in"/>
                    </div>
                </form:form>
            </div>
        </div>
    </div>

    <!-- RATING MODAL TO GATHER FEEDBACK FROM USER-->
    <div class="modal fade" id="ratingModal" tabindex="-1" role="dialog" aria-labelledby="exampleModalLabel">
        <div class="modal-dialog" role="document">
            <div class="modal-content">
                <div class="modal-header">
                    <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
                    <h4 class="modal-title" id="exampleModalLabel"><center>Rating Feedback</center></h4>
                </div>
                <form>
                    <div class="modal-body">
                        <div class="progress">
                            <div class="progress-bar progress-bar-warning progress-bar-striped active" role="progressbar" aria-valuemin="0" aria-valuemax="100"></div>
                        </div>

                        <div class="step">
                            <%
                                if (request.getAttribute("authUser") != null) {
                                    User user = (User) request.getAttribute("authUser");
                            %>
                            <center><h5>Welcome Back! <b><%=user.getEmail()%></b></h5></br>
                            <%}%>
                                <div class="well">
                                    Please fill up the following questions to help us gauge the accuracy of the coloured routes.
                                </div>
                            </center>
                        </div>
                        <div class="step">
                            <center>
                                <div class="well">Do you find the colour coded routes accurate to your needs? </div>
                                <div class="radio">
                                    <label class="radio-inline"><input type="radio" name="optradio">Yes</label>
                                    <label class="radio-inline"><input type="radio" name="optradio">No</label>
                                </div>
                            </center>
                        </div>
                        <div class="step">
                            <center>
                                <div class="well">I find it ____ painful following the colour coded routes.</div>
                                <div class="radio">
                                    <label class="radio-inline"><input type="radio" name="optradio">More</label>
                                    <label class="radio-inline"><input type="radio" name="optradio">Less</label>
                                </div>
                            </center>
                        </div>
                        <div class="step well">
                            <center>Thank you for your input!</center>
                        </div>
                    </div>
                    <div class="modal-footer">
                        <button class="action back btn btn-info">Back</button>
                        <button class="action next btn btn-info">Next</button>
                        <button class="action submit btn btn-success">Submit</button>
                    </div>
                </form>
            </div>
        </div>
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
                <!-- Collection of nav links and other content for toggling -->
                <div id="collapse navbar-collapse" class="collapse navbar-collapse">
                    <!--Replace login button to logout button for successfully logged in user-->
                    <!-- for search bar of locations -->
                    <ul class="nav navbar-nav col-lg-7 col-md-5 col-sm-8">
                    <%--to display logged in username--%>
                    <%
                        if (request.getAttribute("authUser") != null) {
                            User user = (User) request.getAttribute("authUser");
                    %>
                        <p class="navbar-text"><i>Signed in as <b><%=user.getEmail()%></b></i></p>
                    <%}%>
                        <div class="navbar-form navbar-left" role="search">
                            <div class="form-group">
                                <input type="text" class="form-control" id="addressSearch" placeholder="Search">
                            </div>
                            <button type="submit" id="submitSearch" class="btn btn-default">Submit</button>
                        </div>

                        <button type="button" class="btn btn-default" id="gps" onclick="initMap.getLocation()">
                            <span class="glyphicon glyphicon-map-marker" aria-hidden="true"></span>
                        </button>
                    </ul>

                    <ul class="nav navbar-nav navbar-right col-lg-3 col-md-5 col-sm-2">
                        <div class="btn-group" role="group" aria-label="...">
                            <%
                                if (request.getAttribute("authUser") == null) {
                            %>
                                <button type="button" class="btn btn-default" data-toggle="modal" data-target="#exampleModal" id="login">Login
                                </button>
                                <%--for logged in user: logout function--%>
                            <%
                                }else{
                            %>
                                <a href="/process-logout.do"><button class="btn btn-danger" type="button" id="logout">Logout</button></a>
                            <%}%>
                            <button class="btn btn-primary" type="button" id="drop" onclick="initMap.drop()">
                                Obstacle Reported
                            </button>
                        </div>
                    </ul>
            </div> <!--End of row -->
        </div>
    </div>
    </nav>

    <div id="map" style="height: 95%;"></div>

    <% if (request.getAttribute("authUser") != null) { %>
        <div class="alert alert-success alert-dismissible" role="alert">
            <button type="button" class="close" data-dismiss="alert" aria-label="Close"><span aria-hidden="true"><b>&times;</b></span></button>
            <center><strong>Welcome!</strong> You can now click on the map to add obstacle that you have observed.</center>
        </div>
    <%}%>

    <% if (request.getAttribute("errorMsg") != null) { %>
    <div class="alert alert-danger alert-dismissible" role="alert">
        <button type="button" class="close" data-dismiss="alert" aria-label="Close"><span aria-hidden="true"><b>&times;</b></span></button>
        <center><strong>Error!</strong> ${errorMsg}. Please try again.</center>
    </div>
    <%}%>

    <!-- jQuery (necessary for Bootstrap's JavaScript plugins) -->
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.12.4/jquery.min.js"></script>

    <script>
        var map;
        //for drop marker function
        var markers = [];
        //to store obstacle reported markers
        var obstacles = [];

        var defaultLat = 1.362361;
        var defaultLng = 103.814071;
        var defaultZoomLevel = 12;
        //var initialLanding = true;

        //FOR SENSITVITY RATING
        $(document).ready(function () {
            var current = 1;

            widget = $(".step");
            btnnext = $(".next");
            btnback = $(".back");
            btnsubmit = $(".submit");

            // Init buttons and UI
            widget.not(':eq(0)').hide();
            hideButtons(current);
            setProgress(current);

            // Next button click action
            btnnext.click(function (e) {
                if (current < widget.length) {
                    widget.show();
                    widget.not(':eq(' + (current++) + ')').hide();
                    setProgress(current);
                }
                hideButtons(current);
                e.preventDefault();
            })

            // Back button click action
            btnback.click(function (e) {
                if (current > 1) {
                    current = current - 2;
                    btnnext.trigger('click');
                }
                hideButtons(current);
                e.preventDefault();
            })

            <%if (request.getAttribute("authUser") != null) {%>
                $('#ratingModal').modal('toggle');
            <%}%>
        });

        // Change progress bar action
        setProgress = function(currstep){
            var percent = parseFloat(100 / widget.length) * currstep;
            percent = percent.toFixed();
            $(".progress-bar").css("width",percent+"%").html(percent+"%");
        }

        // Hide buttons according to the current step
        hideButtons = function(current){
            var limit = parseInt(widget.length);

            $(".action").hide();

            if(current < limit) btnnext.show();
            if(current > 1) btnback.show();
            if (current == limit) { btnnext.hide(); btnsubmit.show(); }
        }

        //to redirect logged in user to register page
        function Redirect() {
            window.location="/register.do";
        }

        //marker logo - taken from Google Map API tutorial (using it for now, will change in future iterations)
        //will require SVG coding
        var image = 'https://developers.google.com/maps/documentation/javascript/examples/full/images/beachflag.png';

        function initMap() {

            //TO GET CURRENT LOCATION OF THE USER
//            if(initialLanding){
//                getLocation();
//            }
//
//            function getLocation() {
//                if (navigator.geolocation) {
//                    navigator.geolocation.getCurrentPosition(showPosition, showError);
//                } else {
//                    alert("Geolocation is not supported by this browser.");
//                }
//            }

            //for navbar current location subsequent recall
            initMap.getLocation = function getLocation() {
                initialLanding = false;
                if (navigator.geolocation) {
                    navigator.geolocation.getCurrentPosition(showPosition, showError);
                } else {
                    alert("Geolocation is not supported by this browser.");
                }
            }

            function showPosition(position) {
                defaultLat = position.coords.latitude;
                defaultLng = position.coords.longitude;
                defaultZoomLevel = 16;
                initMap();
                //for subsequrnt current location recall
//                if (!initialLanding){
//                    map = new google.maps.Map(document.getElementById('map'), {
//                        center: {lat: defaultLat, lng: defaultLng},
//                        scrollwheel: true,
//                        zoom: 18,
//                    });
//                } else{
//                    //for inital landing page
//                    initialLanding = false;
//                    initMap();
//                }
            }


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

            <c:forEach var="entry1" items="${viewRoutes}">
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


            //FOR SEARCH FUNCTION IN NAVBAR
            var geocoder = new google.maps.Geocoder();

            document.getElementById('submitSearch').addEventListener('click', function() {
                geocodeAddress(geocoder, map);
            });

            //only logged in user can click on the map and add obstacle
            <c:if test="${authUser != null}">
                map.addListener('click', function(e) {
                    placeMarker(e.latLng);
                    //add newly clicked lat log coordinate inside marker array
                    obstacles.push({lat:e.latLng.lat(), lng:e.latLng.lng()});
                });
            </c:if>

            //ADDING MARKERS
            function placeMarker(location) {
                var marker = new google.maps.Marker({
                    position: location,
                    map: map,
                    icon: image
                });

                var reportObstacleDescription =
                        '<div class="container-fluid">' +
                        '<div class="row">' +
                        "<center><h4>Add Obstacle</h4></center>" +
                        '<form id="upload-form" method="POST" enctype="multipart/form-data" action="/process-upload-obstacle.do">' +
                        '<div class="form-group">' +
                        '<input type="hidden" name="email" value="awloong@gmail.com">' +
                        '<input type="hidden" name="lat" value="' + location.lat() +'">' +
                        '<input type="hidden" name="lng" value="' + location.lng() + '">' +
                        '<label for="inputDescription">Description</label>' +
                        '<textarea name="description" form="upload-form" rows="4" cols="50" class="form-control" id="inputDescription" placeholder="Description"></textarea>' +
                        '</div>' +
                        "<h5>Upload Photo</h5><label class='btn btn-default btn-file'>" +
                        '<input id="image" name="file" type="file" accept="image/*" onchange="loadFile(event)" style="display: none;">' +
                        '<label for="image"><img src= "../images/upload.png" class="img-rounded" id="output" style="width:150px; height:150px"/></label>' +
                        '</label>"' +
                        '<p class="help-block">Click to upload</p>' +
                        '<center><button type="submit" class="btn btn-primary">Add Obstacle</button></center>' +
                        '</form>' +
                        '</div></div>';

                var infowindow = new google.maps.InfoWindow({
                    content: reportObstacleDescription
                });

                infowindow.open(map, marker);

                //setting a delay in opening up the infowindow, i.e. in 800 seconda later
                setTimeout(function() { infowindow.open(map, marker) }, 800);

                // READING IMAGE FROM USER INPUT AND DISPLAY ON FORM
                loadFile = function(event) {
                    var output = document.getElementById('output');
                    output.src = URL.createObjectURL(event.target.files[0]);
                };

                //allows marker to open up the infowindow again after being closed
                marker.addListener('click', function () {
                    infowindow.open(map, marker);
                });
            }

            <c:forEach var="entry" items="${obstacles}">
                displayMarkers("${entry.description}", ${entry.latitude}, ${entry.longitude}, "${entry.timestamp}");
            </c:forEach>

            function displayMarkers(desc, lat, lng, ts) {
                var marker = new google.maps.Marker({
                    position: {lat: lat, lng: lng},
                    icon: image,
                    map: map,
                    title: desc
                });

                var obstacleInfo =
                        '<div class="container-fluid">' +
                        '<div class="row">' +
                        '<center><h4>Obstacle Info</h4></center>' +
                        '<p><b>Description:</b></p>' +
                        '<p>' + desc + '</p>' +
                        '<b>Timestamp: </b>' + ts +
                        '<p><center><img src="http://wheelroutes.icitylab.com/rest/obstacle/'+ lat + '/' + lng + '/"  style="width:200px; height:200px"/></center><p>' +
                        '</div></div>';

                var infowindow = new google.maps.InfoWindow({
                    content: obstacleInfo
                });

                marker.addListener('click', function () {
                    infowindow.open(map, marker);
                });
            }

            function getRandomColor() {
                var letters = '0123456789ABCDEF';
                var color = '#';
                for (var i = 0; i < 6; i++ ) {
                    color += letters[Math.floor(Math.random() * 16)];
                }
                return color;
            }

            initMap.drop = function drop() {
                clearMarkers();
                for (var i = 0; i < obstacles.length; i++) {
                    addMarkerWithTimeout(obstacles[i], i * 200);
                }
            }

            function addMarkerWithTimeout(position, timeout) {
                window.setTimeout(function() {
                    markers.push(new google.maps.Marker({
                        position: position,
                        map: map,
                        animation: google.maps.Animation.DROP,
                        icon: image
                    }));
                }, timeout);
            }

            function clearMarkers() {
                for (var i = 0; i < markers.length; i++) {
                    markers[i].setMap(null);
                }
                markers = [];
            }

            // ERORR FOR RETRIEVING USER'S GEOLOCATION
            function showError(error) {
                switch(error.code) {
                    case error.PERMISSION_DENIED:
                        alert("User denied the request for Geolocation.");
                        break;
                    case error.POSITION_UNAVAILABLE:
                        alert("Location information is unavailable.");
                        break;
                    case error.TIMEOUT:
                        alert("The request to get user location timed out.");
                        break;
                    case error.UNKNOWN_ERROR:
                        alert("An unknown error occurred.");
                        break;
                }
            }
        }

        //for search function at the nav bar to allow user to search for text locations within the Google Map
        function geocodeAddress(geocoder, resultsMap) {
            var address = document.getElementById('addressSearch').value;
            geocoder.geocode({'address': address}, function(results, status) {
                if (status === 'OK') {
                    defaultLat = results[0].geometry.location.lat();
                    defaultLng = results[0].geometry.location.lng();
                    defaultZoomLevel = 16;
                    initMap();
                } else {
                    alert('Geocode was not successful for the following reason: ' + status);
                }
            });
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