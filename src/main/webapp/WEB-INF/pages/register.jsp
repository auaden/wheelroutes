<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page isELIgnored="false" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <!--Let browser know website is optimized for mobile-->
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1.0"/>
    <title>Registration</title>

    <!-- CSS  -->
    <link href="https://fonts.googleapis.com/icon?family=Material+Icons" rel="stylesheet">
    <link href="../../css/materialize.css" type="text/css" rel="stylesheet" media="screen,projection"/>
    <link href="../../css/registration_page.css" type="text/css" rel="stylesheet" media="screen,projection"/>

    <script src="https://code.jquery.com/jquery-2.1.1.min.js"></script>
    <script src="../../js/materialize.js"></script>
    <script src="../../js/init.js"></script>
</head>

<body>
<nav class="amber lighten-1" role="navigation">
    <div class="navbar-primary">
        <div class="col s1">
            <a class="btn-floating btn-large waves-effect waves-light tooltipped" data-position="bottom" data-delay="50" data-tooltip="Back"
               href="/landing.do"><i class="material-icons large">undo</i></a>
        </div>
    </div>
</nav>

<div class="section no-pad-bot" id="index-banner">
    <div class="container">
        <br><br>
        <div class = "row center col s4">
            <img id = "bee" src="../../images/bee.png" alt="Humblebee" class="responsive-img">
            <h1 class="header center orange-text">Welcome Onboard!</h1>
            <h5 class="header col s12 dark">Making wheelchair accessbility in Singapore a reality</h5>
        </div>
        <br>
        <div class="container">
            <form:form modelAttribute="user" action="process-registration.do" class="form-signin">

                <!-- EMAIL SETTING-->
                <div class="input-field col s6">
                    <i class="material-icons prefix">email</i>
                    <form:input path="email" id="inputEmail" type="email" class="validate" placeholder="Please include '@' in the email address"/>
                    <form:label path="email" for="emailAddress" data-error="Try again!">Email</form:label>
                </div>
                <br>

                <!-- PASSWORD SETTING -->
                <div class="input-field col s12">
                    <i class="material-icons prefix">vpn_key</i>
                    <form:input path="password" id="password" type="password" class="validate" placeholder="Password is at least 6 numbers long"/>
                    <form:label path="password" for="password">Password</form:label>
                </div>
                <br>

                <button class="btn right" type="submit"><b>Register</b></button>

                <!-- FOR FILE IMAGE UPLOAD -->
                <!-- URL.createObjectURL() on the File from your <input> to prompt window for file upload.
                Pass this URL to img.src to tell the browser to load the provided image. -->
                <%--<input id="image" type="file" accept="image/*" onchange="loadFile(event)">--%>
                <%--<label for="image"><img src= "../../images/upload_image.png" class ="circle responsive-img" id="output"/></label>--%>
                <%--</input>--%>
                <%--<!-- <img id = "bee" src="logos/bee.png" alt="Humblebee" class="responsive-img"> -->--%>
                <%--<script>--%>
                    <%--var loadFile = function(event) {--%>
                        <%--var output = document.getElementById('output');--%>
                        <%--output.src = URL.createObjectURL(event.target.files[0]);--%>
                    <%--};--%>
                <%--</script>--%>

                <!-- END OF FORM -->
            </form:form>
        </div>
    </div>
</div>

<input type="hidden" id="errorMsg" value="${errorMsg}">

<br><br><br>

<footer class="page-footer orange">
    <div class="container">
        <div class="row">

            <div class="col l6 s12">
                <h5 class="white-text">Team Bio</h5>
                <p class="grey-text text-lighten-4">We are a team of like-minded SMU students who have a heart to give back to society. The main goal of the project is to develop an application that enables and empowers wheelchair users in Singapore to be able to travel beyond their routine destinations.</p>
            </div>

            <div class="col s3 m6 l4 offset-l2">
                <img id = "humblebee" src="../../images/team_humblebees.png" alt="Humblebee" class="responsive-img right-align">
            </div>

        </div>
    </div>

    <div class="footer-copyright">
        <div class="container">
            Powered by <a class="orange-text text-lighten-3" href="https://wiki.smu.edu.sg/is480/IS480_Team_wiki%3A_2016T1_HumbleBees">Team HumbleBees</a>
        </div>
    </div>

</footer>

<script>
    var error = document.getElementById("errorMsg").value;
    if (error != null && error.length != 0){
//        window.alert(error);
        Materialize.toast(error, 3000);
    }
</script>

</body>
</html>