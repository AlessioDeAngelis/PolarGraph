package it.uniroma3.dia.polar.utils;

public class StringEscaper {
public static String convert(String input){
	String converted = "";
	if(input==null || input.equals("")){
		converted = " ";
	}else{
		converted = input.replaceAll("'", "");
	}
	return converted;
}
}
