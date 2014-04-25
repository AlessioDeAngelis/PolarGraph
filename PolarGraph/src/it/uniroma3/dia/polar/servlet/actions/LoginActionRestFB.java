package it.uniroma3.dia.polar.servlet.actions;

import it.uniroma3.dia.polar.utils.ApplicationProperty;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient.AccessToken;

public class LoginActionRestFB extends Action {

	@Override
	public String executeAction(HttpServletRequest request)
			throws ServletException {
		AccessToken accessToken =
				  new DefaultFacebookClient().obtainAppAccessToken(ApplicationProperty.FACEBOOK_CLIENT_ID, ApplicationProperty.FACEBOOK_SECURE_KEY);

		String accessTokenString = accessToken.getAccessToken();
		request.getSession().setAttribute("access_token", accessTokenString);
		return "facebook_login_ok";
	}

	private String readURL(URL url) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		InputStream is = url.openStream();
		int r;
		while ((r = is.read()) != -1) {
			baos.write(r);
		}
		return new String(baos.toByteArray());
	}
}
