package workoutdiary;

import java.sql.ResultSet;
import java.sql.SQLException;

public class WorkoutGroup {
	private int id;

	WorkoutGroup(ResultSet rs) throws SQLException {
		this.id = rs.getInt("WorkoutID");
	}
	
	public ResultSet getExercises(DBConn conn) throws SQLException {
		ResultSet _rs = conn.getRow("SELECT * FROM Exercise NATURAL JOIN ExerciseInGroup WHERE GroupID = " + String.valueOf(this.id) + ";");
		return _rs;
	}
}
