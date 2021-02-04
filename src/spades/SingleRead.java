package spades;

import java.io.File;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;

import database.DatabaseConnection;

public class SingleRead {

	public void runSpades(int idproject) {
		try {
			Statement st = null;
			String cmd;
			ResultSet rs;
			cmd = "SELECT * FROM organism WHERE idproject=" + idproject + " AND single is not null;";
			st = DatabaseConnection.connect.createStatement();
			rs = st.executeQuery(cmd);
			String single = rs.getString("single");

			Statement statement;
			String cmmd;
			ResultSet resulSet;
			cmmd = "SELECT * FROM parameter WHERE idproject=" + idproject + ";";
			statement = DatabaseConnection.connect.createStatement();
			resulSet = statement.executeQuery(cmmd);
			String spades_options = resulSet.getString("spades_options");
			String spades_kmers = resulSet.getString("spades_kmers");
			String spades_memory = resulSet.getString("spades_memory");
			String spades_threads = resulSet.getString("spades_threads");
			String output = resulSet.getString("output");

			String assemblyCommand = "python3 " + "/opt/SPAdes/bin/spades.py " + spades_options + " -s " + single
					+ " -k " + spades_kmers + " -m " + spades_memory + " -t " + spades_threads + " -o " + output
					+ "/spades-assembly";

			CommandLine singleSpades = CommandLine.parse(assemblyCommand);
			DefaultExecutor rfExecutor = new DefaultExecutor();
			rfExecutor.execute(singleSpades);

			PreparedStatement preparedStmt = null;
			String result_spades = output + "/spades-assemby/scaffolds.fasta";
			preparedStmt = DatabaseConnection.connect.prepareStatement(
					"UPDATE organism SET result_spades= '" + result_spades + "' WHERE idproject=" + idproject + ";");
			preparedStmt.executeUpdate();
			checkFileSpades(idproject);
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
		}
	}

	public void checkFileSpades(int idproject) {
		try {
			Statement statement;
			String cmmd;
			ResultSet resulSet;
			cmmd = "SELECT * FROM parameter WHERE idproject=" + idproject + ";";
			statement = DatabaseConnection.connect.createStatement();
			resulSet = statement.executeQuery(cmmd);

			String output = resulSet.getString("output");

			File a = new File(output + "/spades-assembly/scaffolds.fasta");
			Thread.sleep(10000);
			if (a.exists()) {
				if (a.length() > 0) {
					System.out.println("SPAdes OK");
				}
				else {
					runSpades(idproject);

				}

			}
			else {
				runSpades(idproject);
			}

			
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
		}
	}

}
