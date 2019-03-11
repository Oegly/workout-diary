package workoutdiary;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;

public class Workout {
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
	}
	
	public String getNote() {
		return this.note;	
	}
	
	public ResultSet getExercises(DBConn conn) throws SQLException {
		ResultSet _rs = conn.getRow("SELECT * FROM Exercise NATURAL JOIN ExerciseInWorkout WHERE WorkoutID = " + String.valueOf(this.id) + ";");
		return _rs;
	}
	
	public String toString() {
		return "Workout " + this.date + ", " + this.time;
	}
	public static void main(String[] args) throws SQLException, ClassNotFoundException {
		DBConn conn = new DBConn("localhost", "Diary", "root", "fish");
		
		ResultSet rs = conn.getLastWorkouts(2);
		
		while (rs.next()) {
			Workout _w = new Workout(rs);
			System.out.println(_w + ": " +_w.getNote());
			
			ResultSet exercises = _w.getExercises(conn);
			
			while (exercises.next()) {
				System.out.println(Exercise.New(exercises.getInt("ExerciseID"), conn));
			}
			
		System.out.println();
		}
		
		System.out.println();
		System.out.println(Exercise.New(1, conn));
	}
}
