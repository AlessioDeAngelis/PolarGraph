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
    

<h1>Choose the recommenders </h1>

<h3>There are several recommenders to try and test. <br> Please answer to the questions after you tested a recommender,  <br> so we can evaluate the system :) 
Some recommenders can take a long,  <br> we plase ask you to test at least recommender #1, #2, #5, #6,#9,#10,#11,#12.  <br> If you have more time you can test even the others :) Thank you!  <br> </h3>

<p>RECOMMENDER 1:<form action="recommend.do" method="post">
<!-- <b><p>Select a social Recommender</p></b> -->
<!-- <p><input type="" value="naive" name="rankerSocialType"/>Naive Social Recommender</p> -->
<!-- <p><input type="radio" value="selected_categories_social" name="rankerSocialType" required/>Selected Categories Social</p> -->
<!-- <p><input type="radio" value="selected_categories_social_collaborative_filtering" name="rankerSocialType"/>Selected Categories Collaborative Filtering</p> -->
<!-- <b><p>Select a DBPEDIA Recommender. It is not mandatory</p></b> -->
<!-- <p><input type="radio" value="semanticbase" name="rankerDbpediaType"/>Semantic Base Recommender</p> -->
<!-- <p><input type="radio" value="semanticclever" name="rankerDbpediaType"/>Semantic Clever Recommender</p> -->
<!-- <p><input type="radio" value="semantic_closer_places" name="rankerDbpediaType"/>Semantic Closer Places Recommender</p> -->
<!-- <b><p>Select an EUROPEANA DBPEDIA Recommender. It is not mandatory</p></b> -->
<!-- <p><input type="radio" value="europeana" name="rankerEuropeanaType"/>Europeana Recommeder</p> -->

<!-- <p><input type="submit" value="Recommend" name="conferma" /></p> -->
<input  readonly name="rankerSocialType" value="select_categories_social"/>
<input readonly name="rankerDbpediaType" value="semantic_closer_places"/>
<input readonly name="recommenderNumber" value="1"/>
<input type="submit" value="Recommend" name="conferma" />
</form>
</p>
<p>RECOMMENDER 2:<form action="recommend.do" method="post">
<input readonly name="rankerSocialType" value="selected_categories_social_collaborative_filtering"/>
<input  readonly name="rankerDbpediaType" value="semantic_closer_places"/>
<input readonly name="recommenderNumber" value="2"/>
<input type="submit" value="Recommend" name="conferma" />
</form>
</p>
<p>RECOMMENDER 3: (it may be slow, be patient :))<form action="recommend.do" method="post">
<input readonly name="rankerSocialType" value="select_categories_social"/>
<input  readonly name="rankerDbpediaType" value="semanticclever"/>
<input readonly name="recommenderNumber" value="3"/>
<input type="submit" value="Recommend" name="conferma" />
</form>
</p>
<p>RECOMMENDER 4: (it may be slow, be patient :))<form action="recommend.do" method="post">
<input readonly name="rankerSocialType" value="selected_categories_social_collaborative_filtering"/>
<input  readonly name="rankerDbpediaType" value="semanticclever"/>
<input readonly name="recommenderNumber" value="4"/>
<input type="submit" value="Recommend" name="conferma" />
</form>
<p>RECOMMENDER 5: <form action="recommend.do" method="post">

<input readonly name="rankerSocialType" value="select_categories_social"/>
<input  readonly name="rankerDbpediaType" value="semantic_closer_places"/>
<input readonly value="europeana" name="rankerEuropeanaType" />
<input readonly name="recommenderNumber" value="5"/>
<input type="submit" value="Recommend" name="conferma" />
</form>
</p>
<p>RECOMMENDER 6:<form action="recommend.do" method="post">
<input readonly name="rankerSocialType" value="selected_categories_social_collaborative_filtering"/>
<input  readonly name="rankerDbpediaType" value="semantic_closer_places"/>
<input readonly value="europeana" name="rankerEuropeanaType" />
<input readonly name="recommenderNumber" value="6"/>
<input type="submit" value="Recommend" name="conferma" />
</form>
</p>
<p>RECOMMENDER 7: (it may be slow, be patient :))<form action="recommend.do" method="post">
<input readonly name="rankerSocialType" value="select_categories_social"/>
<input  readonly name="rankerDbpediaType" value="semanticclever"/>
<input readonly value="europeana" name="rankerEuropeanaType" />
<input readonly name="recommenderNumber" value="7"/>
<input type="submit" value="Recommend" name="conferma" />
</form>
</p>
<p>RECOMMENDER 8: (it may be slow, be patient :))<form action="recommend.do" method="post">
<input readonly name="rankerSocialType" value="selected_categories_social_collaborative_filtering"/>
<input  readonly name="rankerDbpediaType" value="semanticclever"/>
<input readonly value="europeana" name="rankerEuropeanaType" />
<input readonly name="recommenderNumber" value="8"/>
<input type="submit" value="Recommend" name="conferma" />
</form>

<p>RECOMMENDER 9:<form action="recommend.do" method="post">
<input readonly name="rankerSocialType" value="selected_categories_social_collaborative_filtering"/>
<input readonly value="europeana" name="rankerEuropeanaType" />
<input readonly name="recommenderNumber" value="9"/>
<input type="submit" value="Recommend" name="conferma" />
</form>
<p>RECOMMENDER 10:<form action="recommend.do" method="post">
<input readonly name="rankerSocialType" value="selected_categories_social"/>
<input readonly value="europeana" name="rankerEuropeanaType" />
<input readonly name="recommenderNumber" value="10"/>
<input type="submit" value="Recommend" name="conferma" />
</form>
<p>RECOMMENDER 11:<form action="recommend.do" method="post">
<input readonly name="rankerSocialType" value="selected_categories_social_collaborative_filtering"/>
<input readonly name="recommenderNumber" value="11"/>
<input type="submit" value="Recommend" name="conferma" />
</form>
<p>RECOMMENDER 12:<form action="recommend.do" method="post">
<input readonly name="rankerSocialType" value="selected_categories_social"/>
<input readonly name="recommenderNumber" value="12"/>
<input type="submit" value="Recommend" name="conferma" />
</form>
</body>
</html>