package rast;

import java.io.BufferedReader;
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
			String ordered_file = result.getString("ordered_file");


			BufferedReader breader = new BufferedReader(new FileReader(reference));

			String line = breader.readLine();
			String organismName = null;
			
			while(line!= null) {
				if(line.contains("ORGANISM")) {
					String [] vect = line.trim().split("M");
					organismName = vect[1].trim();
				}
				
				line = breader.readLine();
			}

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

				Runtime submit = Runtime.getRuntime();
				String rastCommand = "perl " + "lib/svr_submit_RAST_job.pl" + " --user " + rast_user + " --passwd "
						+ rast_pass + " --fasta " + ordered_file + " --domain " + domain
						+ " --taxon " + taxonId + " --bioname " + bioname + " --genetic_code " + genetic_code;
				Process p = submit.exec(rastCommand);
				
				PreparedStatement stmtAnnotationStart = null;
				stmtAnnotationStart = DatabaseConnection.connect.prepareStatement(
						"UPDATE project SET status = 'Running RAST' WHERE project.idproject=" + idproject + ";");
				stmtAnnotationStart.executeUpdate();
				
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
				
				boolean running = true;

				while (running) {
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
					
					System.out.println("RAST: " + status);

					if(status.equals("complete")) {
						running = false;
					}

					Thread.sleep(60000);
				}

				downloadRast(idproject);
				checkFileRast(idproject);
				
				PreparedStatement stmtAnnotationFinish = null;
				stmtAnnotationFinish = DatabaseConnection.connect.prepareStatement(
						"UPDATE project SET status = 'Complete process' WHERE project.idproject=" + idproject + ";");
				stmtAnnotationFinish.executeUpdate();

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

				PrintWriter pw = new PrintWriter(new FileWriter(output + "/GenTreat/GenTreat.embl"));

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

			BufferedReader br = new BufferedReader(new FileReader(output + "/GenTreat/GenTreat.embl"));
			List<String> list = new ArrayList<>();
			String line = br.readLine();
			while (line != null) {
				list.add(line);
				line = br.readLine();

			}
			if (!list.contains("//") == true) {
				submitRast(idproject);
			}
			br.close();
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
		}
	}

}
