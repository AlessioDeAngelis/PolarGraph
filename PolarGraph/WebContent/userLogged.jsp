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

<p><b>STEP 1 :</b>  <a href="storeFacebookUserData.do" method="post">  Store your info</a></p>
<p><b>STEP 2: </b> <a href="storeFacebookFriendData.do?part=1" method="post">  Store your friends info part 1 </a> <br> <a href="storeFacebookFriendData.do?part=2" method="post">  Part 2 </a> <br> <a href="storeFacebookFriendData.do?part=3" method="post">  Part 3 </a> <br> <a href="storeFacebookFriendData.do?part=4" method="post">  Part 4 </a> </p>
<!-- <p><a href="chooseSocialCategories.jsp"> Go to the recommendation page </a></p> -->
<p>  <b>STEP 3 </b>   <a href="recommenderMain.jsp">Go to the recommendation page </a></p>

<p><a href="clearGraphDatabase.do" method="post"> Remove all your data from our database </a></p>

<h4>GUIDE:</h4>
<h4>First you should click on Step 1: this will save your info. <br> Then click on the step 2 to save your friends info: there are several parts.  <br> More part you click on, better will be the final recommendation. Please, click on one part at the time and go to the next one only when finished.  <br> Each part requires about one hour, so patient. Go around, take a coffee, play the guitar :)  <br>  When you are done with step two, there is the most exciting one, the recommendation part. When you are done testing the system and evaluating it, you can remove all of your data from our database, we like preserving your privacy </h4>

</body>
</html>