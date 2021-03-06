package diary;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class ExerciseGroup extends DiaryEntity {
	private int id;
	private String name;

	public ExerciseGroup(int id, DBConn conn) throws SQLException {
		this(conn.getRow("SELECT * FROM ExerciseGroup WHERE GroupID=" + id + ";"));
	}
	
	ExerciseGroup(ResultSet rs) throws SQLException {
		this.id = rs.getInt("GroupID");
		this.name = rs.getString("Name");
	}
	
	public static ArrayList<ExerciseGroup> list(int n, DBConn conn) throws SQLException {
		ArrayList<ExerciseGroup> ret = new ArrayList<ExerciseGroup>();
		String query = "SELECT * FROM ExerciseGroup ORDER BY Name";
		
		if (n > 0) {
			query = query.concat(" LIMIT " + String.valueOf(n));
		}
		
		query = query.concat(";");
		ResultSet rs = conn.getRows(query);
		
		while(rs.next()) {
			ret.add(new ExerciseGroup(rs));
		}
		
		return ret;
	}
	
	public static ArrayList<ExerciseGroup> search(String s, DBConn conn) throws SQLException {
		ArrayList<ExerciseGroup> ret = new ArrayList<ExerciseGroup>();
		String query = "SELECT * FROM ExerciseGroup WHERE Name LIKE '%" + s + "%';";
		ResultSet rs = conn.getRows(query);
		
		while(rs.next()) {
			ret.add(new ExerciseGroup(rs));
		}
		
		return ret;
	}
	
	public ArrayList<Exercise> getExercises(DBConn conn) throws SQLException {
		ResultSet rs = conn.getRows("SELECT * FROM Exercise NATURAL JOIN ExerciseInGroup WHERE GroupID = " + String.valueOf(this.id) + ";");
		//return rs;
		
		ArrayList<Exercise> lst = new ArrayList<Exercise>();
		
		while (rs.next()) {
			lst.add(Exercise.New(rs.getInt("ExerciseID"), conn));
		}
		
		return lst;
	}
	
	public static ExerciseGroup insert(String name, DBConn conn) throws SQLException {
		String query = "INSERT INTO ExerciseGroup (Name) VALUES (?);";
		
		java.sql.PreparedStatement stm = conn.prepareStatement(query);
		
		stm.setString(1, name);
		
		stm.executeUpdate();
		
		ResultSet rs = stm.getGeneratedKeys();
		rs.next();
		
		return new ExerciseGroup(rs.getInt(1), conn);
	}
	
	public ArrayList<Workout> getWorkouts(DBConn conn) throws SQLException {
		ResultSet rs = conn.getRows("SELECT WorkoutID FROM ExerciseInWorkout NATURAL JOIN ExerciseInGroup WHERE GroupID =" + this.id + " GROUP BY WorkoutID;");
		//return rs;
		
		ArrayList<Workout> lst = new ArrayList<Workout>();
		
		while (rs.next()) {
			lst.add(new Workout(rs.getInt("WorkoutID"), conn));
		}
		
		return lst;
	}
	
	public void addExercise(int exerciseId, DBConn conn) throws SQLException {
		String query = "INSERT INTO ExerciseInGroup VALUES (?, ?);";
		java.sql.PreparedStatement stm = conn.prepareStatement(query);
		
		stm.setInt(1, exerciseId);
		stm.setInt(2, this.id);
		stm.executeUpdate();
	}
	
	public String toString() {
		return "Øvingsgruppe (#" + String.valueOf(this.id)+ ") " + this.name;
	}
	
	public String detailedString(DBConn conn) throws SQLException {
		StringBuilder sb = new StringBuilder();
		
		sb.append("Øvingar i gruppa:\n");
		ArrayList<Exercise> exercises = this.getExercises(conn);		
		for (Exercise _x: exercises) {
			sb.append(" " + _x.toString() + "\n");
		}
		
		sb.append("\nTreningsøkter som inneheld øvingar i denne gruppa:\n");
		ArrayList<Workout> workouts = this.getWorkouts(conn);
		for (Workout _w: workouts) {
			sb.append(" " + _w.toString() + "\n");
		}
		
		return this.toString() + "\n\n" + sb.toString();
	}

	public static void main(String[] args) throws ClassNotFoundException, SQLException {
		DBConn conn = new DBConn("localhost", "Diary", "root", "fish");
		
		System.out.println(new ExerciseGroup(1, conn).getExercises(conn).toString());
	}
}
