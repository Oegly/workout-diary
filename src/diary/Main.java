package diary;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {
	public static void main(String[] args)  {
		try {
			DBConn conn = new DBConn("localhost", "Diary", "root", "fish");
			Conversation c = new Conversation(conn);
			
			String input = "";
			Scanner scan = new Scanner(System.in);
						
			while (!input.equals("quit") && scan.hasNext()) {
				input = scan.nextLine();
				c.feed(input);
				System.out.print(c.getPrompt());
			}
			
			scan.close();

			System.out.println();
		} catch (ClassNotFoundException | SQLException  e) {
			e.printStackTrace();
		}
	}
}
