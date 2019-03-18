package diary;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class WorkoutGroup extends DiaryEntity {
	private int id;
	private String name;

	WorkoutGroup(ResultSet rs) throws SQLException {
		this.id = rs.getInt("GroupID");
		this.name = rs.getString("Name");
	}
	
	public static ArrayList<WorkoutGroup> list(int n, DBConn conn) throws SQLException {
		ArrayList<WorkoutGroup> ret = new ArrayList<WorkoutGroup>();
		String query = "SELECT * FROM WorkoutGroup ORDER BY Name";
		
		if (n > 0) {
			query.concat(" LIMIT " + String.valueOf(n));
		}
		
		query.concat(";");
		ResultSet rs = conn.getRows(query);
		
		while(rs.next()) {
			ret.add(new WorkoutGroup(rs));
		}
		
		return ret;
	}
	
	public static ArrayList<WorkoutGroup> search(String s, DBConn conn) throws SQLException {
		ArrayList<WorkoutGroup> ret = new ArrayList<WorkoutGroup>();
		String query = "SELECT * FROM WorkoutGroup WHERE Name LIKE '%" + s + "%';";
		ResultSet rs = conn.getRows(query);
		
		while(rs.next()) {
			ret.add(new WorkoutGroup(rs));
		}
		
		return ret;
	}
	
	public ArrayList<Exercise> getExercises(DBConn conn) throws SQLException {
		ResultSet rs = conn.getRow("SELECT * FROM Exercise NATURAL JOIN ExerciseInGroup WHERE GroupID = " + String.valueOf(this.id) + ";");
		//return rs;
		
		ArrayList<Exercise> lst = new ArrayList<Exercise>();
		
		while (rs.next()) {
			lst.add(Exercise.New(rs.getInt("ExerciseID"), conn));
		}
		
		return lst;
	}
	
	public String toString() {
		return this.name;
	}

	public static void main(String[] args) throws ClassNotFoundException, SQLException {
		DBConn conn = new DBConn("localhost", "Diary", "root", "fish");
		
		ResultSet rs = conn.getRow("SELECT * FROM ExerciseGroup WHERE GroupID = 1;");
		System.out.println(new WorkoutGroup(rs));
	}
}
