<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>WELCOME IN CICERO</title>
</head>
<body>

<!-- <form action="welcome.do" method="post"> -->
<!-- ciao -->
<!-- <p><input type = "text" name = "nome" /></p> -->
<!-- <p><input type="submit" value="conferma" name="conferma" /></p> -->
<!-- </form> -->
<!-- <form action="fblogin.do" method="post"> -->
<!-- facebook login -->
<!-- <p><input type = "text" name = "nome" /></p> -->
<!-- <p><input type="submit" value="conferma" name="facebookLogin" /></p> -->
<!-- </form> -->
<!-- <form action="https://graph.facebook.com/dialog/oauth?client_id=120449845301&redirect_uri=http://localhost:8080/PolarGraph/fblogin.do&scope=email,read_stream -->
<!-- " method="post"> -->
<!-- facebook login -->
<!-- <p><input type = "text" name = "nome" /></p> -->
<!-- <p><input type="submit" value="conferma" name="facebookLogin" /></p> -->
<!-- </form> -->
<img src="data/images/Cicero.PNG" width="5%" height="5%" />
<h2>Log in your Facebook account in order to use the system</h2>
<!-- <a href="https://graph.facebook.com/oauth/authorize?client_id=120449845301&redirect_uri=http://193.204.161.190:8080/Cicero/fblogin.do&scope=email,read_stream"><img src="data/images/fb_button.jpg" width="35%" height="35%" /> </a>  -->
<!-- <a href="https://graph.facebook.com/oauth/authorize?client_id=120449845301&redirect_uri=http://193.204.161.190:8080/Cicero/fblogin.do&scope=email,read_stream,user_about_me,user_birthday,user_likes,user_checkins,user_friends,user_hometown,user_location,user_status,user_photos,user_relationships,friends_about_me,friends_location,friends_photo_video_tags,friends_status,friends_checkins,friends_photo,friends_likes,read_friendlists"><img src="data/images/fb_button.jpg" width="35%" height="35%" /> </a>  -->
<a href="https://graph.facebook.com/oauth/authorize?client_id=120449845301&redirect_uri=http://193.204.161.190:8080/Cicero/fblogin.do&scope=email,read_stream,user_about_me,user_birthday,user_likes,user_checkins,user_friends,user_hometown,user_location,user_status,user_photos,user_relationships,friends_about_me,friends_location,friends_photo_video_tags,friends_status,friends_checkins,friends_photos,friends_likes,read_friendlists" method="post"><img src="data/images/fb_button.jpg" width="35%" height="35%" /> </a> 

</body>
</html>