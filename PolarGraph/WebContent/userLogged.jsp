<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>User logged</title>
</head>
<body>
Welcome ${facebookUser.firstName} 

<p><a href="storeFacebookUserData.do" method="post"> Store your info </a></p>
<p><a href="storeFacebookFriendData.do" method="post"> Store your friends info </a></p>
<p><a href="chooseSocialCategories.jsp"> Go to the recommendation page </a></p>



</body>
</html>