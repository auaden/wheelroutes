<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page isELIgnored="false" %>
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
    <title>WheelRoutes Registration Page</title>
    <!-- Latest compiled and minified CSS -->
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css" integrity="sha384-BVYiiSIFeK1dGmJRAkycuHAHRg32OmUcww7on3RYdg4Va+PmSTsz/K68vbdEjh4u" crossorigin="anonymous">
    <link href="../../css/registration_page.css" type="text/css" rel="stylesheet" media="screen,projection"/>
    <!-- TOGGLE BUTTON -->
    <link href="https://gitcdn.github.io/bootstrap-toggle/2.2.2/css/bootstrap-toggle.min.css" rel="stylesheet">
    <!-- LINK TO FONT AWESOME -->
    <script src="https://use.fontawesome.com/22342cf468.js"></script>
</head>

<body>
<!-- Fixed navbar -->
<nav role="navigation" class="navbar navbar-default navbar-fixed-top">
    <a href="/landing.do"><button type="button" id="back" class="btn btn-primary">Back</button></a>
</nav>
</br></br></br>

<div class="container-fluid">
    <div class="text-center" id="header">
        <div class = "col-sm-offset-3 col-sm-5">
            <img id = "bee" src="../../images/bee.png" alt="Humblebee" class="responsive-img">
            <h1 class="welcome text-info">Welcome Onboard!</h1>
            <h6 class="welcome-message text-muted">Making wheelchair accessbility in Singapore a reality</h6>
        </div>
    </div>
</div>
</br>

<div class="container-fluid">
    <div class="text-center">
        <%--<form class="form-horizontal">--%>
            <form:form modelAttribute="user" action="process-registration.do" class="form-horizontal">
            <div class="form-group">
                <div class="col-sm-offset-1 col-sm-10">
                    <div class="well well-sm">We would like to know more about your medical history to allow us to colour code the routes according to your needs.</br><b>Information will be kept strictly confidential.</b></div>
                </div>
            </div>
            <div class="form-group">
                <%--<form:input path="email" id="inputEmail" type="email" class="validate" placeholder="Please include '@' in the email address"/>--%>
                <%--<form:label path="email" for="emailAddress" data-error="Try again!">Email</form:label>--%>
                <h4><form:label path="email" for="emailAddress" class="col-sm-offset-1 col-sm-2 control-label">Email</form:label></h4>
                <div class="col-sm-6">
                    <form:input path="email" type="email" class="form-control" placeholder="Please include '@' in the email address"/>
                </div>
            </div>
            <div class="form-group">
                <h4><form:label path="password" for="inputPassword3" class="col-sm-offset-1 col-sm-2 control-label">Password</form:label></h4>
                <div class="col-sm-6">
                    <form:input path="password" type="password" class="form-control" placeholder="Password is at least 6 numbers long"/>
                </div>
            </div>
            <div class="form-group">
                <h4><label for="exppain" class="col-sm-offset-1 col-sm-4 control-label">I experience pain going over bumps</label></h4>
                <input class="col-sm-offset-3 col-sm-2" type="checkbox" data-toggle="toggle" data-on="Yes" data-off="No">
            </div>
            <div class="form-group">
                <h4><label for="havebalance" class="col-sm-offset-1 col-sm-4 control-label">I have poor sitting balance</label></h4>
                <input class="col-sm-offset-3 col-sm-2" type="checkbox" data-toggle="toggle" data-on="Yes" data-off="No">
            </div>
            <div class="form-group">
                <div class="col-sm-offset-8 col-sm-1">
                    <button type="submit" class="btn btn-success">Register</button>
                </div>
            </div>
        </form:form>
    </div>
</div>

<%--<div class="section no-pad-bot" id="index-banner">--%>
    <%--<div class="container">--%>
        <%--<br><br>--%>
        <%--<div class = "row center col s4">--%>
            <%--<img id = "bee" src="../../images/bee.png" alt="Humblebee" class="responsive-img">--%>
            <%--<h1 class="header center orange-text">Welcome Onboard!</h1>--%>
            <%--<h5 class="header col s12 dark">Making wheelchair accessbility in Singapore a reality</h5>--%>
        <%--</div>--%>
        <%--<br>--%>
        <%--<div class="container">--%>
            <%--<form:form modelAttribute="user" action="process-registration.do" class="form-signin">--%>

                <%--<!-- EMAIL SETTING-->--%>
                <%--<div class="input-field col s6">--%>
                    <%--<i class="material-icons prefix">email</i>--%>
                    <%--<form:input path="email" id="inputEmail" type="email" class="validate" placeholder="Please include '@' in the email address"/>--%>
                    <%--<form:label path="email" for="emailAddress" data-error="Try again!">Email</form:label>--%>
                <%--</div>--%>
                <%--<br>--%>

                <%--<!-- PASSWORD SETTING -->--%>
                <%--<div class="input-field col s12">--%>
                    <%--<i class="material-icons prefix">vpn_key</i>--%>
                    <%--<form:input path="password" id="password" type="password" class="validate" placeholder="Password is at least 6 numbers long"/>--%>
                    <%--<form:label path="password" for="password">Password</form:label>--%>
                <%--</div>--%>
                <%--<br>--%>

                <%--<button class="btn right" type="submit"><b>Register</b></button>--%>
            <%--</form:form>--%>
        <%--</div>--%>
    <%--</div>--%>
<%--</div>--%>

<nav class="navbar navbar-default navbar-fixed-bottom">
    <% if (request.getAttribute("errorMsg") != null) { %>
    <div class="alert alert-danger alert-dismissible" role="alert">
        <button type="button" class="close" data-dismiss="alert" aria-label="Close"><span aria-hidden="true"><b>&times;</b></span></button>
        <center><strong>Error!</strong> ${errorMsg}. Please try again.</center>
    </div>
    <%}%>
    <div class="container-fluid">
        <div class="row">
            <div class="col-sm-offset-1 col-sm-6">
                <h3 class="text-danger" style="font-family:Ocie; color:white">Team Bio</h3>
                <p class="text-warning">We are a team of like-minded SMU students who have a heart to give back to society. The main goal of the project is to develop an application that enables and empowers wheelchair users in Singapore to be able to travel beyond their routine destinations.</p>
            </div>
            <div class="col-sm-offset-1 col-sm-2">
                <img id = "humblebee" src="../../images/Team_HumbleBees_Logo.png" alt="Humblebee" class="responsive-img right-align">
            </div>
        </div>
    </div>
</nav>


    <!-- jQuery (necessary for Bootstrap's JavaScript plugins) -->
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.12.4/jquery.min.js"></script>
    <script src="../../js/jquery-ui.min.js"></script>
    <!-- Include all compiled plugins (below), or include individual files as needed -->
    <script src="../../js/bootstrap.js"></script>
    <%--TOGGLE BUTTON--%>
    <script src="https://gitcdn.github.io/bootstrap-toggle/2.2.2/js/bootstrap-toggle.min.js"></script>
</body>
</html>