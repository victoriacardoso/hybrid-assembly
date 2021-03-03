package megahit;

import java.io.File;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import org.apache.commons.exec.ExecuteException;
import org.apache.commons.io.FileUtils;

import database.DatabaseConnection;
import treat.Treatment;

public class PairedRead {
	private String paired1;
	private String paired2;
	private String idorganism;
	private int count;
	private int count2;

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

	public String getIdorganism() {
		return idorganism;
	}

	public void runMegahit(int idproject) {
		try {
			Statement st = null;
			String cmd;
			ResultSet rs;
			cmd = "SELECT * FROM organism WHERE idproject=" + idproject
					+ " AND paired1 is not null AND paired2 is not null;";
			st = DatabaseConnection.connect.createStatement();
			rs = st.executeQuery(cmd);
			while (rs.next()) {
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
					String output = resulSet.getString("output");
					String orientation = resulSet.getString("orientation");
					String mem_flag = resulSet.getString("mem_flag");
					String min_count = resulSet.getString("min_count");
					String megahit_kmers = resulSet.getString("megahit_kmers");

					String forwardReads = "-1";
					String reverseReads = "-2";
					
					PreparedStatement sta = null;
					sta = DatabaseConnection.connect
							.prepareStatement("UPDATE project SET status =  'Running Megahit' WHERE project.idproject="
									+ idproject + ";");
					sta.executeUpdate();
					
					Treatment treatment = new Treatment();
					treatment.treatRead(getPaired1(), getPaired2(), output + "/");
					Thread.sleep(120000);
					checkTreatedFile(output + "/1_treated.fastq", output + "/2_treated.fastq", getPaired1(),
							getPaired2(), output, treatment);
					
					switch (orientation) {

					case "fr":
						String assemblyCommand = "/opt/megahit/bin/megahit " + forwardReads + " " + output
								+ "/1_treated.fastq" + " " + reverseReads + " " + output + "/2_treated.fastq"
								+ " --mem-flag " + mem_flag + " --min-count " + min_count + " --k-list " + megahit_kmers
								+ " -o " + output + "/megahit-assembly";
						
						System.out.println("Megahit assembly started...");
						
						Process p = Runtime.getRuntime().exec(assemblyCommand);
						p.waitFor();

						break;
					case "rf":
						String assemblyCmmd = "/opt/megahit/bin/megahit " + reverseReads + " " + output
								+ "/1_treated.fastq" + " " + forwardReads + " " + output + "/2_treated.fastq" + " -o "
								+ output + "/megahit-assembly";
						
						System.out.println("Megahit assembly started...");
						Process p2 = Runtime.getRuntime().exec(assemblyCmmd);
						p2.waitFor();

						break;

					}
					checkMegahitFile(idproject);
					
					PreparedStatement stat = null;
					stat = DatabaseConnection.connect
							.prepareStatement("UPDATE project SET status =  'Complete Megahit' WHERE project.idproject="
									+ idproject + ";");
					stat.executeUpdate();

				}
			}
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
					} else {
						File megahitDirectory = new File(output + "/megahit-assembly");
						FileUtils.deleteDirectory(megahitDirectory);
						runMegahit(idproject);

					}

				} else {
					File megahitDirectory = new File(output + "/megahit-assembly");
					FileUtils.deleteDirectory(megahitDirectory);
					runMegahit(idproject);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void checkTreatedFile(String treated1, String treated2, String read1, String read2, String output, Treatment treatment)
			throws InterruptedException, ExecuteException, IOException {
		File a = new File(treated1);
		File a1 = new File(treated2);
		Thread.sleep(10000);
		if (a.exists() && a1.exists()) {
			if (a.length() > 0 && a1.length() > 0) {
				System.out.println("Treated files OK");
			} else {
				treatment.treatRead(read1, read2, output);
			}
		} else {
			treatment.treatRead(read1, read2, output);

		}
	}
}
