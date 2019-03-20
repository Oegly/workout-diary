package diary;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public abstract class Exercise extends DiaryEntity {
	protected int id;
	protected String name;
	protected String description;
	protected String table;
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
		return "Øving (#" + String.valueOf(this.id)+ ") " + this.name;
	}
	
	public String detailedString() {
		return this.toString();
	}
	
	public static ArrayList<Exercise> list(int n, DBConn conn) throws SQLException {
		ArrayList<Exercise> ret = new ArrayList<Exercise>();
		String query = "SELECT * FROM Exercise ORDER BY Name";
		
		if (n > 0) {
			query = query.concat(" LIMIT " + String.valueOf(n));
		}
		
		query = query.concat(";");
		ResultSet rs = conn.getRows(query);
		
		while(rs.next()) {
			ret.add(Exercise.New(rs.getInt("ExerciseId"), conn));
		}
		
		return ret;
	}
	
	public static ArrayList<Exercise> search(String s, DBConn conn) throws SQLException {
		ArrayList<Exercise> ret = new ArrayList<Exercise>();
		String query = "SELECT * FROM Exercise WHERE Name LIKE '%" + s + "%';";
		ResultSet rs = conn.getRows(query);
		
		while(rs.next()) {
			ret.add(Exercise.New(rs.getInt("id"), conn));
		}
		
		return ret;
	}
	
	public static void main(String[] args) throws ClassNotFoundException, SQLException {
		//System.out.println(Exercise.New(1, new DBConn("localhost", "Diary", "root", "fish")));
	}
}

class UnequippedExercise extends Exercise {
	protected String table = "Exercise NATURAL JOIN UnequippedExercise";
	
	UnequippedExercise(ResultSet rs) throws SQLException {
		this.id = rs.getInt("ExerciseID");
		this.name = rs.getString("Name");
		this.description = rs.getString("Description");
	}

	public static Exercise insert(String name, String description, DBConn conn) throws SQLException {
		String query = "INSERT INTO Exercise (Name, Equipped) VALUES (?, ?);";
		java.sql.PreparedStatement stm1 = conn.prepareStatement(query);
		
		stm1.setString(1, name);
		stm1.setBoolean(2, false);
		stm1.executeUpdate();
		
		ResultSet rs = stm1.getGeneratedKeys();
		rs.next();
		
		query = "INSERT INTO UnequippedExercise (ExerciseID, Description) VALUES (?, ?);";
		
		java.sql.PreparedStatement stm2 = conn.prepareStatement(query);
		
		stm2.setInt(1, rs.getInt(1));
		stm2.setString(2, description);
		stm2.executeUpdate();
		
		return Exercise.New(rs.getInt(1), conn);
	}

	@Override
	public String getDescription() {
		return this.description;
	}
	
	@Override
	public String detailedString() {
		return this.toString() + "(" + this.description +")";
	}
}

class EquippedExercise extends Exercise {
	protected String table = "Exercise NATURAL JOIN EquippedExercise";
	protected Equipment equipment;
	protected int equipment_id;
	
	EquippedExercise(ResultSet rs) throws SQLException {
		this.id = rs.getInt("ExerciseID");
		this.name = rs.getString("Name");
		this.equipment_id = rs.getInt("EquipmentID");
	}

	public static Exercise insert(String name, Equipment equipment, int weight, int sets, DBConn conn) throws SQLException {
		String query = "INSERT INTO Exercise (Name, Equipped) VALUES (?, ?);";
		java.sql.PreparedStatement stm1 = conn.prepareStatement(query);
		
		stm1.setString(1, name);
		stm1.setBoolean(2, true);
		stm1.executeUpdate();
		
		ResultSet rs = stm1.getGeneratedKeys();
		rs.next();
		
		query = "INSERT INTO EquippedExercise (ExerciseID, EquipmentID, Weight, Sets) VALUES (?, ?, ?, ?);";
		
		java.sql.PreparedStatement stm2 = conn.prepareStatement(query);
		
		stm2.setInt(1, rs.getInt(1));
		stm2.setInt(2, equipment.getId());
		stm2.setInt(3, weight);
		stm2.setInt(4, sets);
		stm2.executeUpdate();
		
		return Exercise.New(rs.getInt(1), conn);
	}

	@Override
	public String getDescription() {
		return this.description;
	}
	
	@Override
	public String detailedString() {
		return this.toString() + "\n"
				+ "Bruk " + this.equipment.getName() + " (" + this.description + ")";
	}
	public void setEquipment(DBConn conn) throws SQLException {
		ResultSet rs = conn.getRow("SELECT * FROM Equipment WHERE EquipmentID = " + String.valueOf(this.equipment_id) + ";");
		this.equipment = new Equipment(rs);
		this.description = this.equipment.getDescription();
	}
}
