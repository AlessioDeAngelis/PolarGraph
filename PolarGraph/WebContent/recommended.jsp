<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1" import="it.uniroma3.dia.cicero.graph.model.*"
	import="java.util.*"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<style>
table,th,td {
	border-collapse: collapse;
	border: 2px solid black;
}

th,td {
	padding: 5px;
}
</style>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Recommendation page</title>
</head>
<body>
	<c:forEach var="concept" items="${recommendedObjects}">
	<h3>YOU LIKE ${concept.why} SO WE RECOMMEND YOU</h3>		
		<ul>
			<li>
				<table>
					<tr>
						<th>TITLE</th>
						<td>${concept.name}</td>
					</tr>
					<c:if test="${!empty concept.creator}">
						<tr>
							<th>CREATOR</th>
							<td> ${concept.creator}</td>
						</tr>
					</c:if>
					<c:if test="${!empty concept.source}">
						<tr>
							<th>SOURCE</th>
							<td>${concept.source}</td>
						</tr>
					</c:if>
					<c:if test="${!empty concept.provider}">
						<tr>
							<th>PROVIDER</th>
							<td>${concept.provider}</td>
						</tr>
					</c:if>
					<c:if test="${!empty concept.mediaUrl}">
						<tr>
							<th>MEDIA</th>
							<td><img src="${concept.mediaUrl}" /></td>
						</tr>
					</c:if>
					<c:if test="${!empty concept.externalLink}">
						<tr>
							<th>LINK</th>
							<td><a href="${concept.externalLink}" />${concept.externalLink}</td>
						</tr>
					</c:if>
				</table>
			</li>
		</ul>
	</c:forEach>
<h2> Do you like those recommended objects? Do they match your interests?</h2>
<h3>Please, give a rating of your satisfation</h3>
<p><form action="storeRecommenderRating.do" method="post">
<input type="radio" name="recommender_rating" value="1" required>Poor, I am not interested in those objects</input><br>
<input type="radio" name="recommender_rating" value="2">Fair</input><br>
<input type="radio" name="recommender_rating" value="3">Average, I am interested in some objects but I really don't like some others</input><br>
<input type="radio" name="recommender_rating" value="4">Good</input><br>
<input type="radio" name="recommender_rating" value="5">Excellent: They exactly match my interests!</input>
<br>
<br>
<h2>Did this recommender help you discover a new place in the list you didn't know before?</h2>
<input type="radio" name="novelty" value="1" required>Strongly disagree</input>
<input type="radio" name="novelty" value="2">Disagree</input>
<input type="radio" name="novelty" value="3">Neither disagree nor agree</input>
<input type="radio" name="novelty" value="4">Agree</input>
<input type="radio" name="novelty" value="5">Strongly agree</input>

<br>
<br>
<h2>Did you discover a new place in the list and you really want to visit it now?</h2>
<input type="radio" name="serendipity" value="1" required>Strongly disagree</input>
<input type="radio" name="serendipity" value="2">Disagree</input>
<input type="radio" name="serendipity" value="4">Neither Disagree nor agree</input>
<input type="radio" name="serendipity" value="4">Agree</input>
<input type="radio" name="serendipity" value="5">Strongly Disagree</input>

<input type="submit" value="SUBMIT"/>
</form></p>
</body>
</html>