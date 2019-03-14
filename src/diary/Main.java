package diary;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class Main {
	public static void main(String[] args)  {
		try {
			DBConn conn = new DBConn("localhost", "Diary", "root", "fish");
			
			ResultSet rs = conn.getLastWorkouts(2);
			
			while (rs.next()) {
				Workout _w = new Workout(rs);
				System.out.println(_w + ": " +_w.getNote());
				
				ArrayList<Exercise> exercises = _w.getExercises(conn);
				
				for (Exercise _x: exercises) {
					System.out.println(_x);
				}
			
			System.out.println();
			}

		} catch (ClassNotFoundException | SQLException  e) {
			e.printStackTrace();
		}
	}
}
