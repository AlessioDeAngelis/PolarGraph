package it.uniroma3.dia.polar.servlet;

import it.uniroma3.dia.polar.utils.ApplicationProperty;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

import com.restfb.Connection;
import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;
import com.restfb.Parameter;
import com.restfb.types.User;
import com.visural.common.IOUtil;

public class FacebookServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	String redirectUri = "http://localhost:8080/CassaScout";

	String[] perms = new String[] { "publish_stream", "email" };

	protected void service(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		String code = request.getParameter("code");

		if (code != null) {
			String red = "https://graph.facebook.com/oauth/access_token?client_id="
					+ ApplicationProperty.FACEBOOK_CLIENT_ID
					+ "&redirect_uri="
					+ redirectUri
					+ "&client_secret="
					+ ApplicationProperty.FACEBOOK_SECURE_KEY + "&code=" + code;

			System.out.println(red);

			URL url = new URL(red);

			String accessToken = null;
			Integer expires = null;

			try {
				String result = readURL(url);

				String[] pairs = result.split("&");
				for (String pair : pairs) {
					String[] kv = pair.split("=");
					if (kv.length != 2) {
						throw new RuntimeException("Unexpected auth response");
					} else {
						if (kv[0].equals("access_token")) {
							accessToken = kv[1];
						}
						if (kv[0].equals("expires")) {
							expires = Integer.valueOf(kv[1]);
						}
					}
				}

			} catch (IOException e) {
				throw new RuntimeException(e);
			}

			request.getSession().setAttribute("accessToken", accessToken);

			FacebookClient facebookClient = new DefaultFacebookClient(
					accessToken);

			User user = facebookClient.fetchObject("luca.santaniello",
					User.class, Parameter.with("fields",
							"id, name, birthday, website, email"));

			request.getSession().setAttribute("user", user);

			Connection<User> myFriends = facebookClient.fetchConnection(
					"me/friends", User.class,
					Parameter.with("fields", "id, name"));
			List<User> users = myFriends.getData();

			request.getSession().setAttribute("contacts", users);

			getServletConfig().getServletContext()
					.getRequestDispatcher("/index.jsp")
					.forward(request, response);
		} else {
			String errorReason = request.getParameter("error_reason");

			if (errorReason != null)
				request.setAttribute("messaggio", errorReason);
			else
				request.setAttribute("messaggio", "Code non presente");

			getServletConfig().getServletContext()
					.getRequestDispatcher("/error").forward(request, response);
		}
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

// questa classe consente di ricavare (e stampare a video) le informazioni
// specifiche per un utente in particolare:
// per comodità utilizziamo il formato json
class UserService {

	public void authFacebookLogin(String accessToken, int expires) {
		try {
			JSONObject resp = new JSONObject(
					IOUtil.urlToString(new URL(
							"https://graph.facebook.com/me?access_token="
									+ accessToken)));
			String id = resp.getString("id");
			String firstName = resp.getString("first_name");
			String lastName = resp.getString("last_name");
			String email = resp.getString("email");

			System.out.println(firstName + " " + lastName);

		} catch (Throwable ex) {
			throw new RuntimeException("failed login", ex);
		}
	}
}