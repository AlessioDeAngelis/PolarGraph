package it.uniroma3.dia.cicero.servlet.actions;

import it.uniroma3.dia.cicero.controller.CiceroFacade;
import it.uniroma3.dia.cicero.dependencyinjection.CiceroServletModule;
import it.uniroma3.dia.cicero.persistance.CypherRepository;
import it.uniroma3.dia.cicero.persistance.FacebookRepository;
import it.uniroma3.dia.cicero.utils.ApplicationProperties;

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
    client_id � corrisponde all�id della nostra applicazione precedentemente registrata sul sito Facebook.
    redirect_uri � l�url alla quale l�utente verr� rediretto dopo aver completato l�autenticazione ed aver autorizzato la nostra applicazione ad accedere alle informazioni riservate. Tale Url deve corrispondere all�Url impostata in fase di registrazione dell�applicazione.
    client_secret � corrispondende alla chiave segreta assegnata durante la fase di registrazione.
    code � il parametro appena ricevuto nell�url di risposta.
http://www.html.it/articoli/interazione-con-facebook/
		 * */
		
		String code = request.getParameter("code");
		String redirectUri = "http://193.204.161.190:8080/Cicero/fblogin.do";
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
		Injector injector = Guice.createInjector(new CiceroServletModule(request.getServletContext()));
		FacebookRepository facebookRepository = injector.getInstance(FacebookRepository.class);
		facebookRepository.setAccessTokenString(accessToken);
		facebookRepository.extendTokenLife();
		User user = facebookRepository.retrieveLoggedUser();
		CiceroFacade ciceroFacade = injector.getInstance(CiceroFacade.class);
		CypherRepository cypherRepository = injector.getInstance(CypherRepository.class);
		cypherRepository.setDbPath("data/db/db_"+ user.getId()+".graph");
		cypherRepository.setDbPath(request.getServletContext().getRealPath("/")+cypherRepository.getDbPath());
		System.out.println(cypherRepository.getDbPath());
		
		request.getSession().setAttribute("facade", ciceroFacade);	
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
