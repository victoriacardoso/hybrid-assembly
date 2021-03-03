package log;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import database.DatabaseConnection;

public class Status {
	public String checkStatus(int idproject) throws SQLException {
		String status = null;
		
		Statement statement;
		String cmmd;
		ResultSet resulSet;
		cmmd = "SELECT * FROM project WHERE idproject=" + idproject + ";";
		statement = DatabaseConnection.connect.createStatement();
		resulSet = statement.executeQuery(cmmd);
		
		while (resulSet.next()) {
			status = resulSet.getString("status");
		}
		
		return status;
	}
}
