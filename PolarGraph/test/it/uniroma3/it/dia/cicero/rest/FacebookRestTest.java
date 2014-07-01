package it.uniroma3.it.dia.cicero.rest;

import it.uniroma3.dia.cicero.rest.RestManager;

public class FacebookRestTest {
	public static void main(String[] args) {
		RestManager restManager = new RestManager();
		String requestMethod = "GET";
		String requestProperty = "application/json";
		String accessToken = "CAAAAHAteyDUBAInaEEveFRlxqblq1afIQKOF6vHIZAYrxRioXuH1ZBrKZCpqBZCzpDnCjFnfPhx0lAyObJ7i2Et8ywr2AMZC6fSt6OVX6CHs5BKpllhh7AF0qp0DYMfv09D9l9RA4QNj5jTM34VLng16PtjZBdxEI1GkBUHV1LBsKIdVCXkgT6LImaf81mMbWV4mulOox3cQZDZD";
		String appId = "120449845301";
		String userId = "1366205360";
//		String urlString ="https://graph.facebook.com/" + appId +"/?fields=auth_dialog_headline,auth_dialog_perms_explanation&access_token="+accessToken;
		String urlString ="https://graph.facebook.com/" + userId +"/feed?access_token="+accessToken;

		String output = restManager.restOperation(urlString, requestMethod, requestProperty);
		System.out.println(output);
		
	}
}
