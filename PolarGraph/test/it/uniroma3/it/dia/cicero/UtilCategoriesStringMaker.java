package it.uniroma3.it.dia.cicero;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class UtilCategoriesStringMaker {
	public static void main(String[] args) {
		List<String> cat = readFile();
		for(String s : cat){
			System.out.println("this.culturalHeritageCategories.add(\"" + s + "\"); ");
		}
	}

	private static List<String> readFile() {
		List<String> cat = new ArrayList<>();

		BufferedReader br = null;

		try {

			String sCurrentLine;

			br = new BufferedReader(new FileReader("data/useful_files/cultural_heritage_related_categories"));

			while ((sCurrentLine = br.readLine()) != null) {
				cat.add(sCurrentLine);
			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null)
					br.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}

		return cat;
	}
}
