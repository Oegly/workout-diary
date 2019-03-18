package diary;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class Equipment extends DiaryEntity {
	private int id;
	private String name;
	private String description;

	Equipment(int id, DBConn conn) throws SQLException {
		ResultSet rs = conn.getRow("SELECT * FROM Equipment WHERE EquipmentID = " + String.valueOf(id) + ";");
		
		this.id = rs.getInt("EquipmentID");
		this.name = rs.getString("Name");
		this.description = rs.getString("Description");
		
	}
	
	Equipment(ResultSet rs) throws SQLException {
		this.id = rs.getInt("EquipmentID");
		this.name = rs.getString("Name");
		this.description = rs.getString("Description");
	}
	
	public int getId() {
		return this.id;
	}
	
	public String getName() {
		return this.name;
	}
	
	public String getDescription() {
		return this.description;
	}
	
	public static ArrayList<Equipment> list(int n, DBConn conn) throws SQLException {
		ArrayList<Equipment> ret = new ArrayList<Equipment>();
		String query = "SELECT * FROM Equipment ORDER BY Name";
		
		if (n > 0) {
			query.concat(" LIMIT " + String.valueOf(n));
		}
		
		query.concat(";");
		ResultSet rs = conn.getRows(query);
		
		while(rs.next()) {
			ret.add(new Equipment(rs));
		}
		
		return ret;
	}
	
	public static ArrayList<Equipment> search(String s, DBConn conn) throws SQLException {
		ArrayList<Equipment> ret = new ArrayList<Equipment>();
		String query = "SELECT * FROM Equipment WHERE Name LIKE '%" + s + "%';";
		ResultSet rs = conn.getRows(query);
		
		while(rs.next()) {
			ret.add(new Equipment(rs));
		}
		
		return ret;
	}
	
	public ArrayList<Exercise> getExercises(DBConn conn) throws SQLException {
		ResultSet rs = conn.getRow("SELECT * FROM Exercise NATURAL JOIN EquippedExercise WHERE EquipmentID = " + String.valueOf(this.id) +";");
		
		ArrayList<Exercise> lst = new ArrayList<Exercise>();
		
		while (rs.next()) {
			lst.add(new EquippedExercise(rs));
		}
		
		return lst;
	}
	
	public String toString() {
		return this.name;
	}
	
	public static void setThing() {
		
	}
	
	public static void main(String[] args) throws ClassNotFoundException, SQLException {
		DBConn conn = new DBConn("localhost", "Diary", "root", "fish");
		
		Equipment _e = new Equipment(3, conn);
		System.out.println(_e + ": " + _e.getExercises(conn));
		
		conn.setRow("INSERT INTO Equipment VALUES ();");
	}
}
