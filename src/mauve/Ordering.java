package mauve;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.commons.io.FileUtils;

import database.DatabaseConnection;

public class Ordering {
	public void OrderContigs(int id) throws SQLException, IOException {
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

		Statement statement;
		String cmmd;
		ResultSet resulSet;

		cmmd = "SELECT * FROM parameter WHERE idproject=" + id + ";";
		statement = DatabaseConnection.connect.createStatement();
		resulSet = statement.executeQuery(cmmd);
		while (resulSet.next()) {
			output = resulSet.getString("output");
		}
		String path = "";
		String pt[] = result_cisa.split("/");

		new File(output + "/Mauve").mkdir();

		Runtime run = Runtime.getRuntime();
		Process p;
		String command1 = "bash lib/mauv.sh " + output + "/Mauve" + " " + reference + " " + result_cisa;

		try {
			PreparedStatement stmtOrder = null;
			stmtOrder = DatabaseConnection.connect.prepareStatement(
					"UPDATE project SET status =  'Running Mauve' WHERE project.idproject=" + id + ";");
			stmtOrder.executeUpdate();

			System.out.println("Starting Mauve");
			p = run.exec(command1);

			BufferedReader br;
			String linha;

			br = new BufferedReader(new InputStreamReader(p.getInputStream()));
			PrintWriter pw = new PrintWriter(new FileWriter(output + "/Mauve/" + "log.txt"));

			while ((linha = br.readLine()) != null) {
				pw.println(linha);
			}
			pw.close();

			p.waitFor();

		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
		}

		Getresult ge = new Getresult();
		path = ge.GetResult(output + "/Mauve/" + "log.txt");

		PreparedStatement preparedStmt = null;
		String ordered_file = output + "/Mauve/" + "alignment" + path.trim() + "/" + pt[pt.length - 1];

		File genTreatFolder = new File(output + "/GenTreat/");
		genTreatFolder.mkdir();
		new File(ordered_file).renameTo(new File(genTreatFolder + "/GenTreat.fasta"));

		preparedStmt = DatabaseConnection.connect.prepareStatement("UPDATE organism SET ordered_file= '"
				+ genTreatFolder + "/GenTreat.fasta" + "' WHERE idproject=" + id + ";");
		preparedStmt.executeUpdate();

		checkFileMauve(id, output, ordered_file, genTreatFolder);

		PreparedStatement stmtOrderFinish = null;
		stmtOrderFinish = DatabaseConnection.connect
				.prepareStatement("UPDATE project SET status =  'Complete Mauve' WHERE project.idproject=" + id + ";");
		stmtOrderFinish.executeUpdate();

	}

	public void checkFileMauve(int id, String output, String ordered_file, File genTreatFolder)
			throws SQLException, IOException {

		File a = new File(genTreatFolder + "/GenTreat.fasta");

		if (a.exists()) {
			if (a.length() > 0) {
				System.out.println("Mauve OK");
			} else {
				FileUtils.deleteDirectory(genTreatFolder);
				OrderContigs(id);
			}

		} else {
			FileUtils.deleteDirectory(genTreatFolder);
			OrderContigs(id);
		}

		System.out.println("OrderingContigs Complete");

	}
}
