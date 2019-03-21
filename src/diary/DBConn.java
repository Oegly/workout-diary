package diary;

import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.mysql.jdbc.PreparedStatement;
import com.mysql.jdbc.ResultSetMetaData;

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
		//System.out.println(db_url + ", " + username + ", " + password);
		Class.forName("org.gjt.mm.mysql.Driver");
		this.connection = DriverManager.getConnection(this.db_url, username, password);
	}
	
	public void close() throws SQLException {
		this.connection.close();
	}
	
	public java.sql.PreparedStatement prepareStatement(String sql, boolean keyReturn) throws SQLException {
		if (keyReturn) {
			return this.connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
		}
		return this.connection.prepareStatement(sql);
	}

	public java.sql.PreparedStatement prepareStatement(String sql) throws SQLException {
		return this.prepareStatement(sql, true);
	}
	
	public ResultSet getRows(String query) throws SQLException {
		return this.connection.createStatement().executeQuery(query);
	}
	
	public ResultSet getRow(String query) throws SQLException {
		ResultSet rs = this.connection.createStatement().executeQuery(query);
		rs.next();
		
		return rs;
	}
			
	public void setRow(String query) throws SQLException {
		this.connection.createStatement().executeQuery(query);
	}
	
	// Få opp informasjon om et antall n sist gjennomførte treningsøkter med notater, der n spesifiseres av brukeren.
	public ResultSet getLastWorkouts(int n) throws SQLException {
		return this.getRows("SELECT * FROM Workout ORDER BY Date, Time DESC LIMIT " + String.valueOf(n) + ";");
	}
	
	public static void main(String[] args) throws SQLException, ClassNotFoundException {
		DBConn conn = new DBConn("localhost", "Diary", "root", "fish");
		
		//conn.insertEquipment("Ergometersykkel", "Du glømmer det aldri!");
		//conn.insertEquippedExercise("Snurring", new Equipment(2, conn), 5, 60);
		//conn.insertUnequippedExercise("Slå hjul", "Veit du kva dette er?");
	}
}
