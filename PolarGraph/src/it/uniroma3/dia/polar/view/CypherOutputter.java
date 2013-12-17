package it.uniroma3.dia.polar.view;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

import org.neo4j.cypher.javacompat.ExecutionResult;

public class CypherOutputter {

	public static void resultWrite(ExecutionResult result, String filename) throws FileNotFoundException, IOException {
		/****************************
		 * display content of all rows in result by columns not used in actual
		 * code
		 ****************************/

		FileWriter fstream = new FileWriter(filename);
		BufferedWriter out = new BufferedWriter(fstream);

		int size = 0; // get final size
		String disp = Integer.toString(size);
		for (String column : result.columns()) {
			disp += ";" + column;
		}
		disp += "\n";

		out.write(disp); // write name of columns

		for (Map<String, Object> row : result) {
			size++;
			disp = Integer.toString(size);
			for (String column : result.columns()) {
				disp += ";" + row.get(column);
			}
			disp += "\n";
			out.write(disp);

		}
		out.close();
		System.out.println("total written elements to " + filename + " : " + size);

	}

	public static void resultDisplay(ExecutionResult result) {
		/****************************
		 * display content of all rows in result by columns not used in actual
		 * code
		 ****************************/

		int size = 0; // get final size

		String disp = "";
		for (String column : result.columns()) {
			disp += column + "\t";
		}
		System.out.println(disp); // display name of columns

		for (Map<String, Object> row : result) {

			disp = "";
			for (String column : result.columns()) {
				disp += row.get(column) + "\t";
			}
			System.out.println(disp);
			size++;
		}
		System.out.println("total elements : " + size);

	}
}
