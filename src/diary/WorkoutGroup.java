package diary;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class WorkoutGroup {
	private int id;

	WorkoutGroup(ResultSet rs) throws SQLException {
		this.id = rs.getInt("WorkoutID");
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
}
