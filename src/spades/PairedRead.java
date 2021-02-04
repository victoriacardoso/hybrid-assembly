package spades;

import java.io.File;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;

import database.DatabaseConnection;

public class PairedRead {
	private String paired1;
	private String paired2;

	public String getPaired1() {
		return paired1;
	}

	public void setPaired1(String paired1) {
		this.paired1 = paired1;
	}

	public String getPaired2() {
		return paired2;
	}

	public void setPaired2(String paired2) {
		this.paired2 = paired2;
	}

	public void runSpades(int idproject) {
		try {
			Statement st = null;
			String cmd;
			ResultSet rs;
			cmd = "SELECT * FROM organism WHERE idproject=" + idproject
					+ " AND paired1 is not null AND paired2 is not null;";
			st = DatabaseConnection.connect.createStatement();
			rs = st.executeQuery(cmd);
			String paired1 = rs.getString("paired1");
			String paired2 = rs.getString("paired2");
			setPaired1(paired1);
			setPaired2(paired2);

			Statement statement;
			String cmmd;
			ResultSet resulSet;
			cmmd = "SELECT * FROM parameter WHERE idproject=" + idproject + ";";
			statement = DatabaseConnection.connect.createStatement();
			resulSet = statement.executeQuery(cmmd);
			while (resulSet.next()) {
				String spades_options = resulSet.getString("spades_options");
				String orientation = resulSet.getString("orientation");
				String spades_kmers = resulSet.getString("spades_kmers");
				String spades_memory = resulSet.getString("spades_memory");
				String spades_threads = resulSet.getString("spades_threads");
				String output = resulSet.getString("output");

				Runtime spadesFolder = Runtime.getRuntime();
				spadesFolder.exec("mkdir " + output + "/spades-assembly/");

				String forwardReads = "-1";
				String reverseReads = "-2";

				switch (orientation) {
				case "fr":
					String assemblyCommand = "python3 " + "/opt/SPAdes/bin/spades.py " + spades_options + " "
							+ forwardReads + " " + output + "/1_treated.fastq" + " " + reverseReads + " " + output
							+ "/2_treated.fastq" + " -k " + spades_kmers + " -m " + spades_memory + " -t "
							+ spades_threads + " -o " + output + "/spades-assembly";

					CommandLine frSpades = CommandLine.parse(assemblyCommand);
					DefaultExecutor frExecutor = new DefaultExecutor();
					frExecutor.execute(frSpades);

					break;
				case "rf":
					String assemblyCmmd = "python3 " + "/opt/SPAdes/bin/spades.py " + spades_options + " "
							+ reverseReads + " " + output + "/1_treated.fastq" + " " + forwardReads + " " + output
							+ "/2_treated.fastq" + " -k " + spades_kmers + " -m " + spades_memory + " -t "
							+ spades_threads + " -o " + output + "spades-assembly";

					CommandLine rfSpades = CommandLine.parse(assemblyCmmd);
					DefaultExecutor rfExecutor = new DefaultExecutor();
					rfExecutor.execute(rfSpades);
					break;
				}
				checkFileSpades(idproject);
				PreparedStatement preparedStmt = null;
				String result_spades = output + "/spades-assemby/scaffolds.fasta";
				preparedStmt = DatabaseConnection.connect.prepareStatement("UPDATE organism SET result_spades= '"
						+ result_spades + "' WHERE idproject=" + idproject + ";");
				preparedStmt.executeUpdate();
			}
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
