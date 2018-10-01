<#-- @ftlvariable name="_csrf" type="org.springframework.security.web.csrf.CsrfToken" -->
<!doctype html>
<html>
<head>
    <title>UOB Todo App</title>
    <meta id="token" name="token" content="${_csrf.token}">
    <link rel="stylesheet" href="/static/css/main.css">
</head>
<body>
<div id="app"></div>
<script src="/static/js/app.js"></script>
</body>
</html>
