package diary;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
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
	
	Workout(ResultSet rs) throws SQLException {
		this.id = rs.getInt("WorkoutID");
		this.note = rs.getString("Note");
		this.date = rs.getDate("Date");
		this.time = rs.getTime("Time");
		this.duration = rs.getInt("Duration");
		this.personalPerformance = rs.getInt("PersonalPerformance");
		this.personalShape = rs.getInt("PersonalShape");
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
		String query = "SELECT * FROM Workout ORDER BY Date, Time DESC";
		
		if (n > 0) {
			query.concat(" LIMIT " + String.valueOf(n));
		}
		
		query.concat(";");
		ResultSet rs = conn.getRows(query);
		
		while(rs.next()) {
			ret.add(new Workout(rs));
		}
		
		return ret;
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
	
	public String toString() {
		return "Trenigsøkt (#" + String.valueOf(this.id)+ ") "  + this.date + ", " + this.time;
	}
	
	public static void main(String[] args) throws SQLException, ClassNotFoundException, InstantiationException, IllegalAccessException {
		DBConn conn = new DBConn("localhost", "Diary", "root", "fish");
		
		ResultSet rs = conn.getLastWorkouts(2);
		
		while (rs.next()) {
			Workout _w = new Workout(rs);
			System.out.println(_w + ": " +_w.getNote());
			
			ArrayList<Exercise> exercises = _w.getExercises(conn);
			
			for (Exercise _x: exercises) {
				System.out.println(_x);
			}
			
		System.out.println();
		}
		
		System.out.println();
		System.out.println(Exercise.New(1, conn));
	}
}
