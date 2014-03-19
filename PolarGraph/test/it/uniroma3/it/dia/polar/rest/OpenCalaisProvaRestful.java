package it.uniroma3.it.dia.polar.rest;

import java.io.IOException;

import mx.bigdata.jcalais.CalaisClient;
import mx.bigdata.jcalais.CalaisObject;
import mx.bigdata.jcalais.CalaisResponse;
import mx.bigdata.jcalais.rest.CalaisRestClient;

public class OpenCalaisProvaRestful {
	public static void main(String[] args) {
		try{
		CalaisClient client = new CalaisRestClient("bs6b86v37tb3ah6patt95fzc");
		String text = "Ciao a tutti oggi sono stato a Roma e ho girato il Colosseo con i miei amici";
		String textEnglish = "I'm in Knoxville and thinking of you guys! Wish you were here and can't wait to see you guys in Italy sometime sooner than later :)\n\n(and happy 1/2 to Eloise!)";
		CalaisResponse response = client
				.analyze(textEnglish);
		
		for (CalaisObject entity : response.getEntities()) {
			System.out.println(entity.getField("_type") + ":" + entity.getField("name"));
		}
	    for (CalaisObject topic : response.getTopics()) {
	        System.out.println(topic.getField("categoryName"));
	      }
	    for (CalaisObject tags : response.getSocialTags()){
	        System.out.println(tags.getField("_typeGroup") + ":" 
	                           + tags.getField("name"));
	      }
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			
		}
	}
}
