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

			System.out.println("SPAdes assembly started...");
			String assemblyCommand = "python3 " + "/opt/SPAdes/bin/spades.py " + spades_options + " -s " + single
					+ " -k " + spades_kmers + " -m " + spades_memory + " -t " + spades_threads + " -o " + output
					+ "/spades-assembly";
			
			PreparedStatement sta = null;
			sta = DatabaseConnection.connect.prepareStatement(
					"UPDATE project SET status =  'Running SPAdes' WHERE project.idproject=" + idproject + ";");
			sta.executeUpdate();
			
			Process p = Runtime.getRuntime().exec(assemblyCommand);
			
			BufferedReader br;
			String linha;

			br = new BufferedReader(new InputStreamReader(p.getInputStream()));
			PrintWriter pw = new PrintWriter(new FileWriter(output + "/log.txt"));

			while ((linha = br.readLine()) != null) {
				pw.println(linha);
			}
			p.waitFor();
			pw.close();

			new File(output + "/log.txt").delete();
			
			checkFileSpades(idproject);
			
			PreparedStatement statmt = null;
			statmt = DatabaseConnection.connect
					.prepareStatement("UPDATE project SET status =  'Complete SPAdes' WHERE project.idproject="
							+ idproject + ";");
			statmt.executeUpdate();
			
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
