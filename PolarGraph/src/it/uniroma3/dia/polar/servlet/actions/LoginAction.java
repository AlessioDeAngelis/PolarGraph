package it.uniroma3.dia.polar.servlet.actions;

import it.uniroma3.dia.dependencyinjection.PolarServletModule;
import it.uniroma3.dia.polar.controller.PolarFacade;
import it.uniroma3.dia.polar.persistance.CypherRepository;
import it.uniroma3.dia.polar.persistance.FacebookRepository;
import it.uniroma3.dia.polar.utils.ApplicationProperties;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.restfb.types.User;

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
		
		//now that the user is logged initialize the guice injector and store the current fb user info
		Injector injector = Guice.createInjector(new PolarServletModule(request.getServletContext()));
		FacebookRepository facebookRepository = injector.getInstance(FacebookRepository.class);
		facebookRepository.setAccessTokenString(accessToken);
		facebookRepository.extendTokenLife();
		User user = facebookRepository.retrieveLoggedUser();
		PolarFacade polarFacade = injector.getInstance(PolarFacade.class);
		CypherRepository repository = injector.getInstance(CypherRepository.class);
		repository.setDbPath("data/db/db_"+ user.getId()+".graph");
		repository.setDbPath(request.getServletContext().getRealPath("/")+repository.getDbPath());
		System.out.println(repository.getDbPath());

		request.getSession().setAttribute("facade", polarFacade);	
		request.getSession().setAttribute("facebookUser", user );	
		request.getSession().setAttribute("fb_user_id", user.getId() );	

		request.getSession().setAttribute("injector", injector);

		
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
