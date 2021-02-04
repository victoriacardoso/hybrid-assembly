package mauve;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import database.DatabaseConnection;

public class Ordering {
	public void OrderContigs(String id) throws SQLException {
		String result_cisa = "";
		String reference = "";
		Statement stmt = null;
		String cmmdo;
		ResultSet result;
		cmmdo = "SELECT * FROM organism WHERE idproject =" + id + ";";
		stmt = DatabaseConnection.connect.createStatement();
		result = stmt.executeQuery(cmmdo);
		while (result.next()) {
			result_cisa = result.getString("result_cisa");
			reference = result.getString("reference");
		}
			String output = "";
			String path = "";
			String pt[] = result_cisa.split("/");

			String arq[] = reference.split("/");
			for (int j = 0; j < arq.length - 1; j++) {
				output = output.concat(arq[j] + "/");
			}
		
		Runtime run = Runtime.getRuntime();
		Process p;
		String command1 = "bash lib/mauv.sh " + output + " " + reference + " " + result_cisa;
		
		try {
			System.out.println("Starting OrderingContigs");
			p = run.exec(command1);

			BufferedReader br;
			String linha;

			br = new BufferedReader(new InputStreamReader(p.getInputStream()));
			PrintWriter pw = new PrintWriter(new FileWriter(output + "log.txt"));

			while ((linha = br.readLine()) != null) {
				pw.println(linha);
			}
			pw.close();

			p.waitFor();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Getresult ge = new Getresult();
		path = ge.GetResult(output + "log.txt");

		System.out.println("Essa Linha é do path --:" + output + "alignment" + path.trim() + "/" + pt[pt.length - 1]);
		System.out.println("OrderingContigs Complete");
	}
}
