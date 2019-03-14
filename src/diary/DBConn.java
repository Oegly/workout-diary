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
	
	public void insertEquipment(String name, String description) throws SQLException {
		String query = "INSERT INTO Equipment (Name, Description) VALUES (?, ?);";
		java.sql.PreparedStatement stm1 = this.connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
		
		stm1.setString(1, name);
		stm1.setString(2, description);
		stm1.executeUpdate();
	}
	
	public void insertUnequippedExercise(String name, String description) throws SQLException {
		String query = "INSERT INTO Exercise (Name, Equipped) VALUES (?, ?);";
		java.sql.PreparedStatement stm1 = this.connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
		
		stm1.setString(1, name);
		stm1.setBoolean(2, false);
		stm1.executeUpdate();
		
		ResultSet rs = stm1.getGeneratedKeys();
		rs.next();
		
		query = "INSERT INTO UnequippedExercise (ExerciseID, Description) VALUES (?, ?);";
		
		java.sql.PreparedStatement stm2 = this.connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
		
		stm2.setInt(1, rs.getInt(1));
		stm2.setString(2, description);
		stm2.executeUpdate();
	}
	
	public void insertEquippedExercise(String name, Equipment equipment, int weight, int sets) throws SQLException {
		String query = "INSERT INTO Exercise (Name, Equipped) VALUES (?, ?);";
		java.sql.PreparedStatement stm1 = this.connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
		
		stm1.setString(1, name);
		stm1.setBoolean(2, true);
		stm1.executeUpdate();
		
		ResultSet rs = stm1.getGeneratedKeys();
		rs.next();
		
		query = "INSERT INTO EquippedExercise (ExerciseID, EquipmentID, Weight, Sets) VALUES (?, ?, ?, ?);";
		
		java.sql.PreparedStatement stm2 = this.connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
		
		stm2.setInt(1, rs.getInt(1));
		stm2.setInt(2, equipment.getId());
		stm2.setInt(3, weight);
		stm2.setInt(4, sets);
		stm2.executeUpdate();
	}
	
	public void setRow(String query) throws SQLException {
		this.connection.createStatement().executeQuery(query);
	}
	
	// Få opp informasjon om et antall n sist gjennomførte treningsøkter med notater, der n spesifiseres av brukeren.
	public ResultSet getLastWorkouts(int n) throws SQLException {
		return this.getRow("SELECT * FROM Workout ORDER BY Date, Time DESC LIMIT " + String.valueOf(n) + ";");
	}
	
	public static void main(String[] args) throws SQLException, ClassNotFoundException {
		DBConn conn = new DBConn("localhost", "Diary", "root", "fish");
		
		conn.insertEquipment("Ergometersykkel", "Du glømmer det aldri!");
		//conn.insertEquippedExercise("Snurring", new Equipment(2, conn), 5, 60);
		//conn.insertUnequippedExercise("Slå hjul", "Veit du kva dette er?");
	}
}
