<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"
    import="it.uniroma3.dia.polar.controller.*"   %>
    <% PolarFacade facade = (PolarFacade)session.getAttribute("facade");
    String id = (String)session.getAttribute("fb_user_id");%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Insert title here</title>
</head>
<body>
    

HOMEPAGE il tuo id è ${fb_user_id}

<form action="recommend.do" method="post">
<p><input type="submit" value="Recommend" name="conferma" /></p>
</form>
</body>
</html>