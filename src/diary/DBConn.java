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
		
		Class.forName("org.gjt.mm.mysql.Driver");
		this.connection = DriverManager.getConnection(this.db_url, username, password);
	}
	
	public ResultSet getRows(String query) throws SQLException {
		return this.connection.createStatement().executeQuery(query);
	}
	
	public ResultSet getRow(String query) throws SQLException {
		ResultSet rs = this.connection.createStatement().executeQuery(query);
		rs.next();
		
		return rs;
	}
	
	public Equipment insertEquipment(String name, String description) throws SQLException {
		String query = "INSERT INTO Equipment (Name, Description) VALUES (?, ?);";
		java.sql.PreparedStatement stm = this.connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
		
		stm.setString(1, name);
		stm.setString(2, description);
		stm.executeUpdate();
		
		ResultSet rs = stm.getGeneratedKeys();
		rs.next();
		
		return new Equipment(rs.getInt(1), this);
	}
	
	public Exercise insertUnequippedExercise(String name, String description) throws SQLException {
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
		
		return Exercise.New(rs.getInt(1), this);
	}
	
	public Exercise insertEquippedExercise(String name, Equipment equipment, int weight, int sets) throws SQLException {
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
		
		return Exercise.New(rs.getInt(1), this);
	}
	
	public Workout insertWorkout(String date, String time, int duration, int shape, int performance, String note) throws SQLException {
		String query = "INSERT INTO Workout (Date, Time, Duration, PersonalShape, PersonalPerformance, Note) VALUES (?, ?, ?, ?, ?, ?);";
		
		java.sql.PreparedStatement stm = this.connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
		
		stm.setString(1, date);
		stm.setString(2, time);
		stm.setInt(3, duration);
		stm.setInt(4, shape);
		stm.setInt(5, performance);
		stm.setString(6, note);
		
		stm.executeUpdate();
		
		ResultSet rs = stm.getGeneratedKeys();
		rs.next();
		
		return new Workout(rs.getInt(1), this);
	}
	
	public ExerciseGroup insertGroup(String name) throws SQLException {
		String query = "INSERT INTO ExerciseGroup (Name) VALUES (?);";
		
		java.sql.PreparedStatement stm = this.connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
		
		stm.setString(1, name);
		
		stm.executeUpdate();
		
		ResultSet rs = stm.getGeneratedKeys();
		rs.next();
		
		return new ExerciseGroup(rs.getInt(1), this);
	}
	
	public void setRow(String query) throws SQLException {
		this.connection.createStatement().executeQuery(query);
	}
	
	public String addExerciseToGroup(int exerciseId, int groupId) throws SQLException {
		String query = "INSERT INTO ExerciseInGroup VALUES (?, ?);";
		java.sql.PreparedStatement stm = this.connection.prepareStatement(query);
		
		stm.setInt(1, exerciseId);
		stm.setInt(2, groupId);
		stm.executeUpdate();

		return new ExerciseGroup(groupId, this).detailedString(this);
	}

	public String addExerciseToWorkout(int exerciseId, int workoutId) throws SQLException {
		String query = "INSERT INTO ExerciseInWorkout VALUES (?, ?);";
		java.sql.PreparedStatement stm = this.connection.prepareStatement(query);
		
		stm.setInt(1, exerciseId);
		stm.setInt(2, workoutId);
		stm.executeUpdate();

		return new Workout(workoutId, this).detailedString(this);
	}
	
	// Få opp informasjon om et antall n sist gjennomførte treningsøkter med notater, der n spesifiseres av brukeren.
	public ResultSet getLastWorkouts(int n) throws SQLException {
		return this.getRows("SELECT * FROM Workout ORDER BY Date, Time DESC LIMIT " + String.valueOf(n) + ";");
	}
	
	public static void main(String[] args) throws SQLException, ClassNotFoundException {
		DBConn conn = new DBConn("localhost", "Diary", "root", "fish");
		
		conn.insertEquipment("Ergometersykkel", "Du glømmer det aldri!");
		//conn.insertEquippedExercise("Snurring", new Equipment(2, conn), 5, 60);
		//conn.insertUnequippedExercise("Slå hjul", "Veit du kva dette er?");
	}
}
