package diary;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.util.ArrayList;

import com.mysql.fabric.xmlrpc.base.Array;

public class Workout extends DiaryEntity {
	private int id;
	private Date date;
	private Time time;
	private int duration;
	private int personalShape;
	private int personalPerformance;
	private String note;
	//private ArrayList<Exercise> exercises;
	
	Workout(int id, DBConn conn) throws SQLException {
		this(conn.getRow("SELECT * FROM Workout WHERE WorkoutID =" + id + ";"));
	}
	
	Workout(ResultSet rs) throws SQLException {
		this.id = rs.getInt("WorkoutID");
		this.note = rs.getString("Note");
		this.date = rs.getDate("Date");
		this.time = rs.getTime("Time");
		this.duration = rs.getInt("Duration");
		this.personalPerformance = rs.getInt("PersonalPerformance");
		this.personalShape = rs.getInt("PersonalShape");
		
		//this.exercises = this.getExercises(conn);
	}
	
	public int getId() {
		return this.id;
	}
	
	public String getDate() {
		return this.date.toString();
	}
	
	public int getPersonalShape() {
		return this.personalShape;
	}
	
	public int getPersonalPerformance() {
		return this.personalPerformance;
	}
	
	public String getNote() {
		return this.note;	
	}
	
	public ArrayList<Exercise> getExercises(DBConn conn) throws SQLException {
		ResultSet rs = conn.getRows("SELECT * FROM Exercise NATURAL JOIN ExerciseInWorkout WHERE WorkoutID = " + String.valueOf(this.id) + ";");
		
		ArrayList<Exercise> lst = new ArrayList<Exercise>();
		
		while (rs.next()) {
			lst.add(Exercise.New(rs.getInt("ExerciseID"), conn));
		}
		
		return lst;	
	}
	
	public static ArrayList<Workout> list(int n, DBConn conn) throws SQLException {
		ArrayList<Workout> ret = new ArrayList<Workout>();
		String query = "SELECT * FROM Workout ORDER BY Date DESC, Time DESC";
		
		if (n > 0) {
			query = query.concat(" LIMIT " + String.valueOf(n));
		}
		
		query = query.concat(";");
		ResultSet rs = conn.getRows(query);
		
		while(rs.next()) {
			ret.add(new Workout(rs));
		}
		
		return ret;
	}
	
	public static Workout insert(String date, String time, int duration, int shape, int performance, String note, DBConn conn) throws SQLException {
		String query = "INSERT INTO Workout (Date, Time, Duration, PersonalShape, PersonalPerformance, Note) VALUES (?, ?, ?, ?, ?, ?);";
		
		java.sql.PreparedStatement stm = conn.prepareStatement(query, true);
		
		stm.setString(1, date);
		stm.setString(2, time);
		stm.setInt(3, duration);
		stm.setInt(4, shape);
		stm.setInt(5, performance);
		stm.setString(6, note);
		
		stm.executeUpdate();
		
		ResultSet rs = stm.getGeneratedKeys();
		rs.next();
		
		return new Workout(rs.getInt(1), conn);
	}
	
	public void addExercise(int exerciseId, DBConn conn) throws SQLException {
		String query = "INSERT INTO ExerciseInWorkout VALUES (?, ?);";
		java.sql.PreparedStatement stm = conn.prepareStatement(query);
		
		stm.setInt(1, exerciseId);
		stm.setInt(2, this.id);
		stm.executeUpdate();
	}
	
	public static ArrayList<Workout> search(String s, DBConn conn) throws SQLException {
		ArrayList<Workout> ret = new ArrayList<Workout>();
		String query = "SELECT * FROM Workout WHERE Notes LIKE '%" + s + "%';";
		ResultSet rs = conn.getRows(query);
		
		while(rs.next()) {
			ret.add(new Workout(rs));
		}
		
		return ret;
	}
	
	public static String getInterval(String start, String end, DBConn conn) {
		try {
			String query = "SELECT * FROM Workout WHERE Date BETWEEN '" + start + "' AND '" + end +"' ORDER BY Date DESC, Time DESC;";
			ResultSet rs = conn.getRows(query);
			
			StringBuilder sb = new StringBuilder();
			sb.append("Resultatlogg for intervallet " + start + " - " + end + " : (#, Dato, Form, Yting)\n");
			
			while(rs.next()) {
				Workout _w = new Workout(rs);
				sb.append("#" + _w.id + " " + _w.date + ": " + _w.personalShape + ", " + _w.personalPerformance + "\n");
			}
			
			return sb.toString();
		} catch (SQLException e) {
			System.out.println(e);
			return "Prøv å skrive datoane på formatet yyyy-mm-dd";
		}
	}
	
	public String toString() {
		return "Trenigsøkt (#" + String.valueOf(this.id)+ ") "  + this.date + ", " + this.time
				+ "\n  " + this.note;
	}
	
	public String detailedString(DBConn conn) throws SQLException {
		StringBuilder sb = new StringBuilder();
		
		ArrayList<Exercise> exercises = this.getExercises(conn);
		
		for (Exercise _x: exercises) {
			sb.append(" " + _x.toString() + "\n");
		}
		
		return "Treningsøkt #" + String.valueOf(this.id)
		+ "\nStart: " + this.date + ", " + this.time
		+ "\nVarigheit: " + this.duration + " minutt"
		+ "\nPersonleg innsats: " + this.personalPerformance
		+ "\nPersonleg form: " + this.personalShape + "\n\n"
		+ "Notat:\n  " + this.note + "\n\n"
		+ "Utførte øvingar:\n" + sb.toString() + "\n\n";
	}
	
	public static void main(String[] args) throws SQLException, ClassNotFoundException, InstantiationException, IllegalAccessException {
		DBConn conn = new DBConn("localhost", "Diary", "root", "fish");
		
		System.out.println(Workout.list(0, conn));
		System.out.println(Workout.getInterval("2019-03-31", "2019-04-01", conn));
		
	}
}
