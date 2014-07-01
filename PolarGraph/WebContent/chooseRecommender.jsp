<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"
    import="it.uniroma3.dia.cicero.controller.*"   %>
    <%
    	CiceroFacade facade = (CiceroFacade)session.getAttribute("facade");
        String id = (String)session.getAttribute("fb_user_id");
    %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Insert title here</title>
</head>
<body>
    

<header>Choose the recommenders </header>
<form action="recommend.do" method="post">
<b><p>Select a social Recommender</p></b>
<p><input type="radio" value="naive" name="rankerSocialType"/>Naive Social Recommender</p>
<p><input type="radio" value="selected_categories_social" name="rankerSocialType" required/>Selected Categories Social</p>
<p><input type="radio" value="selected_categories_social_collaborative_filtering" name="rankerSocialType"/>Selected Categories Collaborative Filtering</p>
<b><p>Select a DBPEDIA Recommender. It is not mandatory</p></b>
<p><input type="radio" value="semanticbase" name="rankerDbpediaType"/>Semantic Base Recommender</p>
<p><input type="radio" value="semanticclever" name="rankerDbpediaType"/>Semantic Clever Recommender</p>
<p><input type="radio" value="semantic_closer_places" name="rankerDbpediaType"/>Semantic Closer Places Recommender</p>
<b><p>Select an EUROPEANA DBPEDIA Recommender. It is not mandatory</p></b>
<p><input type="radio" value="europeana" name="rankerEuropeanaType"/>Europeana Recommeder</p>

<p><input type="submit" value="Recommend" name="conferma" /></p>
</form>

</body>
</html>