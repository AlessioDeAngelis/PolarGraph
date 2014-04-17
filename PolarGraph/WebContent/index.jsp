<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Insert title here</title>
</head>
<body>
This is the homepage

<form action="welcome.do" method="post">
ciao
<p><input type = "text" name = "nome" /></p>
<p><input type="submit" value="conferma" name="conferma" /></p>
</form>
<form action="fblogin.do" method="post">
facebook login
<p><input type = "text" name = "nome" /></p>
<p><input type="submit" value="conferma" name="facebookLogin" /></p>
</form>
<form action="https://graph.facebook.com/dialog/oauth?client_id=120449845301&redirect_uri=http://localhost:8080/PolarGraph/fblogin.do&scope=email,read_stream
" method="post">
facebook login
<p><input type = "text" name = "nome" /></p>
<p><input type="submit" value="conferma" name="facebookLogin" /></p>
</form>

<a href="https://graph.facebook.com/oauth/authorize?client_id=120449845301&redirect_uri=http://localhost:8080/PolarGraph/fblogin.do&scope=email,read_stream"> Click here</a>
</body>
</html>