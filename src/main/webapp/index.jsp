<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Tomcat Docker App</title>
    <link rel="stylesheet" href="assets/css/style.css">
</head>
<body>
    <div class="container">
        <h1>Welcome to Tomcat 9 + Gradle + Docker</h1>
        <p>Message: ${message != null ? message : 'Hello from JSP!'}</p>
        <p>App Version: ${applicationScope.appVersion}</p>

        <form action="hello" method="post">
            <label for="name">Enter your name:</label>
            <input type="text" id="name" name="name" required>
            <button type="submit">Submit</button>
        </form>

        <div id="result"></div>
    </div>

    <script>
        document.querySelector('form').addEventListener('submit', async (e) => {
            e.preventDefault();
            const name = document.getElementById('name').value;
            const response = await fetch('/hello?name=' + encodeURIComponent(name));
            const text = await response.text();
            document.getElementById('result').innerHTML = text;
        });
    </script>
</body>
</html>
