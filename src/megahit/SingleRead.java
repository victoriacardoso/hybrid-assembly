package megahit;

import java.io.File;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import org.apache.commons.io.FileUtils;

import database.DatabaseConnection;

public class SingleRead {
	private String single;
	private String idorganism;
	private File newFolder;
	private int count = 0;
	private int count2 = 0;

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public int getCount2() {
		return count2;
	}

	public void setCount2(int count2) {
		this.count2 = count2;
	}

	public File getNewFolder() {
		return newFolder;
	}

	public void setNewFolder(File newFolder) {
		this.newFolder = newFolder;
	}

	public String getSingle() {
		return single;
	}

	public void setSingle(String single) {
		this.single = single;
	}

	public String getIdorganism() {
		return idorganism;
	}

	public void setIdorganism(String idorganism) {
		this.idorganism = idorganism;
	}

	public void runMegahit(int idproject) {
		try {
			Statement st = null;
			String cmd;
			ResultSet rs;
			cmd = "SELECT * FROM organism WHERE idproject=" + idproject + " AND single is not null;";
			st = DatabaseConnection.connect.createStatement();
			rs = st.executeQuery(cmd);
			while (rs.next()) {
				String single = rs.getString("single");
				setSingle(single);
			}
			Statement statement;
			String cmmd;
			ResultSet resulSet;
			cmmd = "SELECT * FROM parameter WHERE idproject=" + idproject + ";";
			statement = DatabaseConnection.connect.createStatement();
			resulSet = statement.executeQuery(cmmd);
		
			String output = null;
			String mem_flag = null;
			String min_count = null;
			String megahit_kmers = null;
			
			while (resulSet.next()) {
				output = resulSet.getString("output");
				mem_flag = resulSet.getString("mem_flag");
				min_count = resulSet.getString("min_count");
				megahit_kmers = resulSet.getString("megahit_kmers");
			}
				String assemblyCommand = "/opt/megahit/bin/megahit" + " -r " + getSingle() + " --mem-flag " + mem_flag
						+ " --min-count " + min_count + " --k-list " + megahit_kmers + " -o " + output
						+ "/megahit-assembly";
				
				PreparedStatement sta = null;
				sta = DatabaseConnection.connect
						.prepareStatement("UPDATE project SET status =  'Running Megahit' WHERE project.idproject="
								+ idproject + ";");
				sta.executeUpdate();

				System.out.println("Megahit assembly started...");

				Process p = Runtime.getRuntime().exec(assemblyCommand);				
				p.waitFor();
				
				checkMegahitFile(idproject);
				
				PreparedStatement stat = null;
				stat = DatabaseConnection.connect
						.prepareStatement("UPDATE project SET status =  'Complete Megahit' WHERE project.idproject="
								+ idproject + ";");
				stat.executeUpdate();
			
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
		}
	}

	public void checkMegahitFile(int idproject) {
		try {

			Statement statement;
			String cmmd;
			ResultSet resulSet;
			cmmd = "SELECT * FROM parameter WHERE idproject=" + idproject + ";";
			statement = DatabaseConnection.connect.createStatement();
			resulSet = statement.executeQuery(cmmd);
			while (resulSet.next()) {
				String output = resulSet.getString("output");
				File a = new File(output + "/megahit-assembly/final.contigs.fa");
				Thread.sleep(10000);
				if (a.exists()) {
					if (a.length() > 0) {
						System.out.println("Megahit OK");
					}
					else {
						File megahitDirectory = new File(output + "/megahit-assembly");
						FileUtils.deleteDirectory(megahitDirectory);
						runMegahit(idproject);
						
					}
				}
				else {
					File megahitDirectory = new File(output + "/megahit-assembly");
					FileUtils.deleteDirectory(megahitDirectory);
					runMegahit(idproject);
				}
			}
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
		}
	}
}
