package diary;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public abstract class Exercise {
	protected int id;
	protected String name;
	protected String description;
	protected boolean equipped;
	
	public static Exercise New(int id, DBConn conn) throws SQLException {
		ResultSet rs = conn.getRow("SELECT * FROM Exercise WHERE ExerciseID = " + String.valueOf(id) + ";");
	
		if (rs.getBoolean("Equipped")) {
			ResultSet _rs = conn.getRow("SELECT * FROM Exercise NATURAL JOIN EquippedExercise WHERE ExerciseID = " + String.valueOf(id) + ";");
			EquippedExercise _ex = new EquippedExercise(_rs);
			
			_ex.setEquipment(conn);
			
			return _ex;
		}
		
		ResultSet _rs = conn.getRow("SELECT * FROM Exercise NATURAL JOIN UnequippedExercise WHERE ExerciseID = " + String.valueOf(id) + ";");
		
		return new UnequippedExercise(_rs);
}
	
	public abstract String getDescription();
	
	public String toString() {
		return this.name;
	}
	
	public static void main(String[] args) throws ClassNotFoundException, SQLException {
		//System.out.println(Exercise.New(1, new DBConn("localhost", "Diary", "root", "fish")));
	}
}

class UnequippedExercise extends Exercise {
	UnequippedExercise(ResultSet rs) throws SQLException {
		this.id = rs.getInt("ExerciseID");
		this.name = rs.getString("Name");
		this.description = rs.getString("Description");
	}

	@Override
	public String getDescription() {
		return this.description;
	}
}

class EquippedExercise extends Exercise {
	protected Equipment equipment;
	protected int equipment_id;
	
	EquippedExercise(ResultSet rs) throws SQLException {
		this.id = rs.getInt("ExerciseID");
		this.name = rs.getString("Name");
		this.equipment_id = rs.getInt("EquipmentID");
	}

	@Override
	public String getDescription() {
		return this.description;
	}
	
	public void setEquipment(DBConn conn) throws SQLException {
		ResultSet rs = conn.getRow("SELECT * FROM Equipment WHERE EquipmentID = " + String.valueOf(this.equipment_id) + ";");
		this.equipment = new Equipment(rs);
		this.description = this.equipment.getDescription();
	}
}
