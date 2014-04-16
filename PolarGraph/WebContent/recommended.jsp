<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"
    import="it.uniroma3.dia.polar.graph.model.*" 
    import="java.util.*" %>
<%@ taglib prefix="c" 
           uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Recommendation page</title>
</head>
<body>
<h1>RECOMMENDED OBJECTS FOR YOU</h1>
<c:forEach var = "object" items= "${recommendedObjects}">
<ul>
<li>${object.name}</li>
</ul>
</c:forEach>

</body>
</html>