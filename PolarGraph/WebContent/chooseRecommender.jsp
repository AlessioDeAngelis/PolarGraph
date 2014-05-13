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
<p><input type="radio" value="naive" name="rankerType"/>Naive Social Recommender</p>
<p><input type="radio" value="selected_categories_social" name="rankerType"/>Selected Categories Social</p>
<p><input type="radio" value="selected_categories_social_collaborative_filtering" name="rankerType"/>Selected Categories Collaborative Filtering</p>
<p><input type="radio" value="semanticbase" name="rankerType"/>Semantic Base Recommender</p>
<p><input type="radio" value="semanticclever" name="rankerType"/>Semantic Clever Recommender</p>
<p><input type="radio" value="semantic_closer_places" name="rankerType"/>Semantic Closer Places Recommender</p>
<p><input type="radio" value="europeana" name="rankerType"/>Europeana Recommeder</p>

<p><input type="submit" value="Recommend" name="conferma" /></p>
</form>
</form>
</body>
</html>