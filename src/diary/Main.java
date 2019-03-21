package diary;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {
	public static void main(String[] args)  {
		try {
			Conversation c = new Conversation();
			Scanner scan = new Scanner(System.in);
						
			String input;
			while (!c.isDone() && scan.hasNext()) {
				input = scan.nextLine();
				c.feed(input);
				System.out.print(c.getPrompt());
			}
			
			scan.close();
			
			System.out.println("Velkommen tilbake! :)");
		} catch (SQLException  e) {
			e.printStackTrace();
		}
	}
}
