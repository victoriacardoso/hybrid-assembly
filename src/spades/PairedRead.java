package spades;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import org.apache.commons.io.FileUtils;

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
				
				PreparedStatement sta = null;
				sta = DatabaseConnection.connect.prepareStatement(
						"UPDATE project SET status =  'Running SPAdes' WHERE project.idproject=" + idproject + ";");
				sta.executeUpdate();
				
				switch (orientation) {
				case "fr":
					String assemblyCommand = "python3 " + "/opt/SPAdes/bin/spades.py " + spades_options + " "
							+ forwardReads + " " + output + "/1_treated.fastq" + " " + reverseReads + " " + output
							+ "/2_treated.fastq" + " -k " + spades_kmers + " -m " + spades_memory + " -t "
							+ spades_threads + " -o " + output + "/spades-assembly";
					
					Process p = Runtime.getRuntime().exec(assemblyCommand);
					System.out.println("SPAdes assembly started...");
					
					BufferedReader br;
					String linha;

					br = new BufferedReader(new InputStreamReader(p.getInputStream()));
					PrintWriter pw = new PrintWriter(new FileWriter(output + "/log.txt"));

					while ((linha = br.readLine()) != null) {
						pw.println(linha);
					}
					pw.close();
					p.waitFor();

					new File(output + "/log.txt").delete();
					br.close();
					
					break;
				case "rf":
					String assemblyCmmd = "python3 " + "/opt/SPAdes/bin/spades.py " + spades_options + " "
							+ reverseReads + " " + output + "/1_treated.fastq" + " " + forwardReads + " " + output
							+ "/2_treated.fastq" + " -k " + spades_kmers + " -m " + spades_memory + " -t "
							+ spades_threads + " -o " + output + "spades-assembly";
					

					Process p2 = Runtime.getRuntime().exec(assemblyCmmd);
										
					BufferedReader br2;
					String linha2;

					br2 = new BufferedReader(new InputStreamReader(p2.getInputStream()));
					PrintWriter pw2 = new PrintWriter(new FileWriter(output + "/log.txt"));

					while ((linha2 = br2.readLine()) != null) {
						pw2.println(linha2);
					}
					pw2.close();
					p2.waitFor();

					new File(output + "/log.txt").delete();
					br2.close();
				}
				checkFileSpades(idproject);
				
				PreparedStatement statmt = null;
				statmt = DatabaseConnection.connect
						.prepareStatement("UPDATE project SET status =  'Complete SPAdes' WHERE project.idproject="
								+ idproject + ";");
				statmt.executeUpdate();
				
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
					File spadesDirectory = new File(output + "/spades-assembly");
					FileUtils.deleteDirectory(spadesDirectory);
					runSpades(idproject);

				}

			}
			else {
				File spadesDirectory = new File(output + "/spades-assembly");
				FileUtils.deleteDirectory(spadesDirectory);
				runSpades(idproject);
			}

			
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
		}
	}
}
