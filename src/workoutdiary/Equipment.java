package workoutdiary;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Equipment {
	private int id;
	private String name;
	private String description;
	
	Equipment(ResultSet rs) throws SQLException {
		this.id = rs.getInt("EquipmentID");
		this.name = rs.getString("Name");
		this.description = rs.getString("Description");
	}
	
	public int getID() {
		return this.id;
	}
	
	public String getName() {
		return this.name;
	}
	
	public String getDescription() {
		return this.description;
	}
	
	public String toString() {
		return this.name;
	}
}
