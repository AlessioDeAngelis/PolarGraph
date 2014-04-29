package it.uniroma3.dia.polar.servlet.actions;

import it.uniroma3.dia.polar.utils.ApplicationProperties;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

public class LoginAction extends Action {

	@Override
	public String executeAction(HttpServletRequest request)
			throws ServletException {
		
		/**
		 * 
    client_id – corrisponde all’id della nostra applicazione precedentemente registrata sul sito Facebook.
    redirect_uri – l’url alla quale l’utente verrà rediretto dopo aver completato l’autenticazione ed aver autorizzato la nostra applicazione ad accedere alle informazioni riservate. Tale Url deve corrispondere all’Url impostata in fase di registrazione dell’applicazione.
    client_secret – corrispondende alla chiave segreta assegnata durante la fase di registrazione.
    code – il parametro appena ricevuto nell’url di risposta.
http://www.html.it/articoli/interazione-con-facebook/
		 * */
		
		String code = request.getParameter("code");
		String redirectUri = "http://localhost:8080/PolarGraph/fblogin.do";
		String accessToken = "";
		Integer expires = null;

		if (code != null) {
			String red = "https://graph.facebook.com/oauth/access_token?client_id="
					+ ApplicationProperties.FACEBOOK_CLIENT_ID
					+ "&redirect_uri="
					+ redirectUri
					+ "&client_secret="
					+ ApplicationProperties.FACEBOOK_SECURE_KEY + "&code=" + code;
	


			URL url = null;
			try {
				url = new URL(red);
			} catch (MalformedURLException e1) {
				e1.printStackTrace();
			}


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
		}
		request.getSession().setAttribute("access_token", accessToken);
		request.getSession().setAttribute("expires", expires);
		return "facebook_login_ok";
	}

	/**
	 * for reading the url and returning it as a string
	 * */
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
