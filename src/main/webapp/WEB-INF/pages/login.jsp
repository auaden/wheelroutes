<%--
  Created by IntelliJ IDEA.
  User: adenau
  Date: 10/9/16
  Time: 1:53 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<html>
<head>
    <title>Login</title>
</head>
    <body>
        <form:form modelAttribute="user" action="process-login.do">
            <form:input path="email" type="email" placeholder="Email Address"/>
            <form:input path="password" type="password" placeholder="Password"/>
            <input type="submit" value="submit">Sign in</input>
        </form:form>
    </body>
</html>
