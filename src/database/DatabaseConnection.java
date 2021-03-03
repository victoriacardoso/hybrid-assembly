package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DatabaseConnection {
	public static Connection connect = null;

	public void connectDatabase() {

		try {
			Class.forName("org.sqlite.JDBC");
			connect = DriverManager.getConnection("jdbc:sqlite:database.db");
			
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.out.println("Connection error");
			System.exit(0);
		}
		
	}

	public void createProject() {
		try {
			Statement stmt = DatabaseConnection.connect.createStatement();
			String sql = "CREATE TABLE IF NOT EXISTS project" + "(idproject INTEGER PRIMARY KEY     AUTOINCREMENT,"
					+ "name TEXT NOT NULL," + "status TEXT NULL," + "createdate TEXT NOT NULL)";
			stmt.executeUpdate(sql);
			stmt.close();
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		}
	}

	public void createOrganism() {
		try {
			Statement stmt = DatabaseConnection.connect.createStatement();
			String sql = "CREATE TABLE IF NOT EXISTS organism" + "(idorganism INTEGER PRIMARY KEY AUTOINCREMENT,"
					+ "single VARCHAR NULL," + "paired1 VARCHAR NULL," + "paired2 VARCHAR NULL,"
					+ "result_cisa VARCHAR NULL," + "reference VARCHAR NULL," + "ordered_file VARCHAR NULL," + "idproject INT NOT NULL,"
					+ "FOREIGN KEY(idproject) REFERENCES PROJECT(idproject));";
			stmt.executeUpdate(sql);
			stmt.close();
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		}
	}

	public void createParameters() {
		try {
			Statement stmt = DatabaseConnection.connect.createStatement();
			String sql = "CREATE TABLE IF NOT EXISTS parameter" + "(idparameter INTEGER PRIMARY KEY     AUTOINCREMENT ,"
					+ "spades_options VARCHAR NULL," + "orientation VARCHAR NULL," + "spades_kmers VARCHAR NULL,"
					+ "spades_memory VARCHAR NULL," + "spades_threads VARCHAR NULL," + "mem_flag VARCHAR NULL,"
					+ "min_count VARCHAR NULL," + "megahit_kmers VARCHAR NULL," + "cisaMinLength VARCHAR NULL,"
					+ "cisaGenomeSize VARCHAR NULL," + "cisaR2Gap VARCHAR NULL," + "output VARCHAR NULL,"
					+ "domain VARCHAR NULL," + "taxonId VARCHAR NULL," + "rast_user VARCHAR NULL,"
					+ "rast_pass VARCHAR NULL," + "genetic_code VARCHAR NULL," + "bioname VARCHAR NULL,"
					+ "job_id VARCHAR NULL," + "" + "idproject INT NOT NULL,"
					+ "FOREIGN KEY(idproject) REFERENCES PROJECT(idproject))";

			stmt.executeUpdate(sql);
			stmt.close();
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		}
	}

	public void insertProject(String name) {
		try {
			connect = DriverManager.getConnection("jdbc:sqlite:database.db");
			PreparedStatement stmt = DatabaseConnection.connect
					.prepareStatement("insert into project (name, createdate) values (?,?)");

			stmt.setString(1, name);
			Date date = new Date();
			DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
			String dateActual = dateFormat.format(date);
			stmt.setString(2, dateActual);
			stmt.executeUpdate();
			stmt.close();
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			e.printStackTrace();
			System.exit(0);
		}
	}

	public void insertOrganism(String single, String paired1, String paired2, String reference, String assemblyresult) {
		try {
			PreparedStatement stmt = DatabaseConnection.connect.prepareStatement(
					"insert into organism (single, paired1, paired2, reference, result_cisa, idproject) values (?,?,?,?,?,?)");
			stmt.setString(1, single);
			stmt.setString(2, paired1);
			stmt.setString(3, paired2);
			stmt.setString(4, reference);
			stmt.setString(5, assemblyresult);
			Statement stm = null;
			String cmdo;
			ResultSet rsult;
			cmdo = "SELECT * FROM project;";
			stm = DatabaseConnection.connect.createStatement();
			rsult = stm.executeQuery(cmdo);
			while (rsult.next()) {
				String idproject = rsult.getString("idproject");
				stmt.setString(6, idproject);
			}

			stmt.executeUpdate();
			stmt.close();
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		}
	}

	public void insertParameters(String spades_options, String orientation, String spades_kmers, String spades_memory,
			String spades_threads, String output, String mem_flag, String min_count, String megahit_kmers,
			String taxonid, String rast_user, String rast_pass, String cisaMinLength, String cisaGenomeSize,
			String cisaR2Gap) {

		try {
			PreparedStatement stmt = DatabaseConnection.connect.prepareStatement(
					"insert into parameter (spades_options,orientation, spades_kmers, spades_memory, spades_threads, output, mem_flag, min_count, megahit_kmers, cisaMinLength, cisaGenomeSize, cisaR2Gap, domain, taxonid, rast_user, rast_pass, genetic_code, idproject) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
			stmt.setString(1, spades_options);
			stmt.setString(2, orientation);
			stmt.setString(3, spades_kmers);
			stmt.setString(4, spades_memory);
			stmt.setString(5, spades_threads);
			stmt.setString(6, output);
			stmt.setString(7, mem_flag);
			stmt.setString(8, min_count);
			stmt.setString(9, megahit_kmers);
			stmt.setString(10, cisaMinLength);
			stmt.setString(11, cisaGenomeSize);
			stmt.setString(12, cisaR2Gap);
			stmt.setString(13, "Bacteria");
			stmt.setString(14, taxonid);
			stmt.setString(15, rast_user);
			stmt.setString(16, rast_pass);
			stmt.setString(17, "11");

			Statement st = null;
			String cmd;
			ResultSet rs;
			cmd = "SELECT * FROM project;";
			st = DatabaseConnection.connect.createStatement();
			rs = st.executeQuery(cmd);
			while (rs.next()) {
				String idproject = rs.getString("idproject");

				stmt.setString(18, idproject);
			}
			stmt.executeUpdate();
			stmt.close();
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		}
	}

	public void checkFieldsDataBase() {
		try {
			Statement stmt = null;
			ResultSet rs;

			stmt = connect.createStatement();
			String cmd = "SELECT * FROM PROJECT;";
			rs = stmt.executeQuery(cmd);

			while (rs.next()) {
				if (rs.getString("status") == null) {
					Statement stmt_delfield = connect.createStatement();
					String cmdd = "DELETE FROM project WHERE idproject=" + rs.getString("idproject") + ";";
					stmt_delfield.executeUpdate(cmdd);
					
					Statement st = connect.createStatement();
					String cmd0 = "DELETE FROM organism WHERE idproject=" + rs.getString("idproject") + ";";
					st.executeUpdate(cmd0);
					
					Statement stm = connect.createStatement();
					String cmmd = "DELETE FROM parameter WHERE idproject=" + rs.getString("idproject") + ";";
					stm.executeUpdate(cmmd);
				}
			}
			stmt.close();
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		}
	}

	public void start() {
		connectDatabase();
		createProject();
		createOrganism();
		createParameters();
	}
}
