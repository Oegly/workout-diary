package workoutdiary;

import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class DBConn {
	private String driver;
	private String server;
	private String database;
	private String db_url;
	
	private Connection connection;
	
	DBConn(String server, String database, String username, String password) throws SQLException, ClassNotFoundException {
		this.server = server;
		this.database = database;
				
		this.db_url = "jdbc:mysql://" + server + "/" + database + "?useSSL=false";
		
		Class.forName("org.gjt.mm.mysql.Driver");
		this.connection = DriverManager.getConnection(this.db_url, username, password);
	}
	
	public ResultSet getRow(String query) throws SQLException {
		return this.connection.createStatement().executeQuery(query);
	}
	
	// Få opp informasjon om et antall n sist gjennomførte treningsøkter med notater, der n spesifiseres av brukeren.
	public ResultSet getLastWorkouts(int n) throws SQLException {
		return this.getRow("SELECT * FROM Workout ORDER BY Date, Time DESC LIMIT " + String.valueOf(n) + ";");
	}
	
	public static void main(String[] args) throws SQLException, ClassNotFoundException {
		DBConn con = new DBConn("localhost", "Diary", "root", "fish");
		
		ResultSet rs = con.getLastWorkouts(2);
		
		while (rs.next()) {
			System.out.println(rs.getString("Note"));
		}
	}

}
