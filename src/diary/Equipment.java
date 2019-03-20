package diary;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class Equipment extends DiaryEntity {
	private int id;
	private String name;
	private String description;

	Equipment(int id, DBConn conn) throws SQLException {
		this(conn.getRow("SELECT * FROM Equipment WHERE EquipmentID = " + String.valueOf(id) + ";"));
		
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
			query = query.concat(" LIMIT " + String.valueOf(n));
		}
		
		query = query.concat(";");
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
		ResultSet rs = conn.getRows("SELECT * FROM Exercise NATURAL JOIN EquippedExercise WHERE EquipmentID = " + String.valueOf(this.id) +";");
		
		ArrayList<Exercise> lst = new ArrayList<Exercise>();
		
		while (rs.next()) {
			lst.add(new EquippedExercise(rs));
		}
		
		return lst;
	}
	
	public static Equipment insert(String name, String description, DBConn conn) throws SQLException {
		String query = "INSERT INTO Equipment (Name, Description) VALUES (?, ?);";
		java.sql.PreparedStatement stm = conn.prepareStatement(query, true);
		
		stm.setString(1, name);
		stm.setString(2, description);
		stm.executeUpdate();
		
		ResultSet rs = stm.getGeneratedKeys();
		rs.next();
		
		return new Equipment(rs.getInt(1), conn);
	}
	
	public String toString() {
		return "Treningsapparat (#" + String.valueOf(this.id)+ ") " + this.name;
	}
	
	public String detailedString(DBConn conn) throws SQLException {
		StringBuilder sb = new StringBuilder();
		
		ArrayList<Exercise> exercises = this.getExercises(conn);
		
		for (Exercise _x: exercises) {
			sb.append(" " + _x.toString() + "\n");
		}
		
		return this.toString() + "\n" + this.description + "\n\n"
				+ "Brukast i følgande øvingar:\n" + sb.toString();
	}
	
	public static void setThing() {
		
	}
	
	public static void main(String[] args) throws ClassNotFoundException, SQLException {
		DBConn conn = new DBConn("localhost", "Diary", "root", "fish");
		
		System.out.println(new Equipment(2, conn).detailedString(conn));
		
		//conn.setRow("INSERT INTO Equipment VALUES ();");
	}
}
