<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1" import="it.uniroma3.dia.polar.graph.model.*"
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
	<h1>YOU LIKE ${recommendedObjects[0].why} SO WE RECOMMEND YOU</h1>
	<c:forEach var="concept" items="${recommendedObjects}">
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

</body>
</html>