package rast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import database.DatabaseConnection;

public class Rast {
	private Integer idProject;
	private String job_id;
	private String status;

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Integer getIdProject() {
		return idProject;
	}

	public void setIdProject(Integer idProject) {
		this.idProject = idProject;
	}

	public String getJob_id() {
		return job_id;
	}

	public void setJob_id(String job_id) {
		this.job_id = job_id;
	}

	public void submitRast(int idproject) {
		try {
			Statement stmt = null;
			String cmmdo;
			ResultSet result;
			cmmdo = "SELECT * FROM organism WHERE idproject =" + idproject + ";";
			stmt = DatabaseConnection.connect.createStatement();
			result = stmt.executeQuery(cmmdo);
			String reference = result.getString("reference");

			BufferedReader breader = new BufferedReader(new FileReader(reference));

			String line = breader.readLine();

			String[] vect = line.split(" ");
			String organismName = vect[1] + " " + vect[2];

			PreparedStatement preparedStmt = null;
			preparedStmt = DatabaseConnection.connect.prepareStatement(
					"UPDATE parameter SET bioname= '" + organismName + "' WHERE idproject=" + idproject + ";");
			preparedStmt.executeUpdate();

			breader.close();

			Statement statement;
			String cmmd;
			ResultSet resulSet;
			cmmd = "SELECT * FROM parameter WHERE idproject=" + idproject + ";";
			statement = DatabaseConnection.connect.createStatement();
			resulSet = statement.executeQuery(cmmd);

			while (resulSet.next()) {
				String domain = resulSet.getString("domain");
				String taxonId = resulSet.getString("taxonId");
				String rast_user = resulSet.getString("rast_user");
				String rast_pass = resulSet.getString("rast_pass");
				String genetic_code = resulSet.getString("genetic_code");
				String bioname = resulSet.getString("bioname");
				String output = resulSet.getString("output");

				Runtime submit = Runtime.getRuntime();
				String rastCommand = "perl " + "lib/svr_submit_RAST_job.pl" + " --user " + rast_user + " --passwd "
						+ rast_pass + " --fasta " + output + "/GenTreat/GenTreat.fasta" + " --domain " + domain
						+ " --taxon " + taxonId + " --bioname " + bioname + " --genetic_code " + genetic_code;
				Process p = submit.exec(rastCommand);
				BufferedReader br;
				String linha;

				br = new BufferedReader(new InputStreamReader(p.getErrorStream()));
				while (((linha = br.readLine()) != null) && ((linha = br.readLine()) != "\n")) {
					String[] fields = linha.split("'");
					String job_id = fields[1];
					setJob_id(job_id);

					PreparedStatement preparedStatement = null;
					preparedStatement = DatabaseConnection.connect.prepareStatement(
							"UPDATE parameter SET job_id=" + getJob_id() + " WHERE idproject=" + idproject + ";");
					preparedStatement.executeUpdate();

					linha = br.readLine();

				}
				PreparedStatement stmtAnnotationStart = null;
				stmtAnnotationStart = DatabaseConnection.connect.prepareStatement(
						"UPDATE project SET status = 'Running RAST' WHERE project.idproject=" + idproject + ";");
				stmtAnnotationStart.executeUpdate();
				statusRast(idproject);

			}
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
		}
	}

	public void statusRast(int idproject) {
		try {
			Statement statement;
			String cmmd;
			ResultSet resulSet;
			cmmd = "SELECT * FROM parameter WHERE idproject=" + idproject + ";";
			statement = DatabaseConnection.connect.createStatement();
			resulSet = statement.executeQuery(cmmd);

			while (resulSet.next()) {
				String rast_user = resulSet.getString("rast_user");
				String rast_pass = resulSet.getString("rast_pass");
				String job_id = resulSet.getString("job_id");

				do {
					Runtime submit = Runtime.getRuntime();
					String rastCommand = "perl " + "lib/svr_status_of_RAST_job.pl " + rast_user + " " + rast_pass + " "
							+ job_id;
					Process p = submit.exec(rastCommand);
					BufferedReader br;
					String linha;

					br = new BufferedReader(new InputStreamReader(p.getInputStream()));
					linha = br.readLine();

					String[] fields = linha.split(":");
					String status = fields[1].trim();
					setStatus(status);
					System.out.println("RAST: " + status);

					Thread.sleep(60000);

				} while (getStatus().equals("running"));

				downloadRast(idproject);
				checkFileRast(idproject);

			}
		} catch (Exception e) {
			e.printStackTrace();
}
	}

	public void downloadRast(int idproject) {
		try {
			Statement statement;
			String cmmd;
			ResultSet resulSet;
			cmmd = "SELECT * FROM parameter WHERE idproject=" + idproject + ";";
			statement = DatabaseConnection.connect.createStatement();
			resulSet = statement.executeQuery(cmmd);
			while (resulSet.next()) {
				String rast_user = resulSet.getString("rast_user");
				String rast_pass = resulSet.getString("rast_pass");
				String job_id = resulSet.getString("job_id");
				String output = resulSet.getString("output");

				File rastDirectory = new File(output + "/RAST");
				rastDirectory.mkdir();

				PrintWriter pw = new PrintWriter(new FileWriter(output + "/RAST/annotated-file.embl"));

				Runtime runDownload = Runtime.getRuntime();
				String rastCommand = "perl " + "lib/svr_retrieve_RAST_job.pl " + rast_user + " " + rast_pass + " "
						+ job_id + " EMBL";
				Process p = runDownload.exec(rastCommand);

				BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(p.getInputStream()));
				String linha;

				while ((linha = bufferedReader.readLine()) != null) {
					pw.println(linha);
				}
				pw.close();

			}
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
		}
	}

	public void checkFileRast(int idproject) {
		try {
			Statement statement;
			String cmmd;
			ResultSet resulSet;
			cmmd = "SELECT * FROM parameter WHERE idproject=" + idproject + ";";
			statement = DatabaseConnection.connect.createStatement();
			resulSet = statement.executeQuery(cmmd);

			String output = resulSet.getString("output");

			BufferedReader br = new BufferedReader(new FileReader(output + "/RAST/annotated-file.embl"));
			List<String> list = new ArrayList<>();
			String line = br.readLine();
			while (line != null) {
				list.add(line);
				line = br.readLine();

			}
			if (list.contains("//") == true) {

				PreparedStatement stmtAnnotationFinish = null;
				stmtAnnotationFinish = DatabaseConnection.connect.prepareStatement(
						"UPDATE project SET status = 'Complete process' WHERE project.idproject=" + idproject + ";");
				stmtAnnotationFinish.executeUpdate();
				


			} else {
				submitRast(idproject);
			}
			br.close();
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
		}
	}

}
