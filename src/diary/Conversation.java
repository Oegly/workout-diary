package diary;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

import javax.swing.text.html.HTMLDocument.HTMLReader.IsindexAction;

import diary.Conversation.State;

public class Conversation {
	enum State {
		INITIAL, LIST, SEARCH, CREATE, DETAIL, REMOVE, ADD, APPEND_GROUP, APPEND_WORKOUT
	}

	HashMap<State, String> command = new HashMap<State, String>();
	HashMap<State, String> helpMessage = new HashMap<State, String>();
	HashMap<State, String> example = new HashMap<State, String>();

	String prompt = " > ";
	String error = "Det forstod eg ikkje heilt...";
	State state;
	State prevState;
	DBConn conn;
	
	Conversation(DBConn conn) {
		this.conn = conn;
		
		this.helpMessage.put(State.INITIAL, "Kva vil du gjere?\n\n"
				+ "lst = Sjå ei liste over element\n"
				+ "src = Søk etter element\n"
				+ "dtl = Sjå detaljert informasjon om eit element\n"
				+ "crt = Sett inn eit nytt element\n"
				+ "etg = Registrer ei øving i ei gruppe\n"
				+ "etw = Registrer ei øving i ei treningsøkt\n\n"
				+ "Meir avanserte funksjonar:\n"
				+ "win = Sjå resultatlogg økter i eit gitt tidsintervall\n"
				+ "wog = Sjå kva for grupper som har vore representert i ei treningsøkt\n"
				+ "");
		this.helpMessage.put(State.LIST, "Skriv kva for element du vil sjå ei liste over, og eventuelt kor mange.\n\n"
				+ "w = Treningsøkt\n"
				+ "e = Apparat\n"
				+ "x = Øving\n"
				+ "g = Øvingsgruppe\n");
		this.helpMessage.put(State.DETAIL, "");
		
		this.example.put(State.LIST, "Om du til dømes vil sjå dei tre siste treningsøktene, skriv \"w 3\".");
		this.example.put(State.INITIAL, "");
				
		System.out.print("Velkommen til treningsdagboka di! ");
		this.transition(State.INITIAL);
		System.out.println(this.getPrompt());
	}
	
	public String getPrompt() {
		return this.prompt;
	}
	
	public void feed(String input) throws SQLException {
		this.evaluate(input);
	}
	
	private void evaluate(String input) throws SQLException {
		switch (input) {
		case ("back"): this.transition(State.INITIAL); return;
		case ("help"): this.help(); return;
		case ("quit"): System.out.println("Velkommen tilbake!"); System.exit(0);
		}
		
		switch (this.state) {
		case INITIAL: this.welcomeScreen(input); break;
		case LIST: System.out.println(this.list(input)); break;
		case DETAIL: System.out.println(this.detail(input)); break;
		
		default: this.welcomeScreen(input); break;
		}
	}
	
	private void transition(State state) {
		this.prevState = this.state;
		this.state = state;
		
		System.out.println(this.helpMessage.get(state));
	}
	
	private void welcomeScreen(String input) {
		if ("lst".equals(input)) {
			this.transition(State.LIST);;
		} else if ("crt".equals(input)){
			this.state = State.CREATE;
		} else if ("src".equals(input)) {
			this.state = State.SEARCH;
		} else if ("dtl".equals(input)) {
			this.state = State.DETAIL;
		} else {
			System.out.println(this.breakdown());
		}
	}
	
	private String list(String input) throws SQLException {
		String[] words = input.split(" ");
		
		int i;
		
		try {
			i = Integer.parseInt(words[1]);
		} catch (IndexOutOfBoundsException | NumberFormatException e) {
			i = 0;
		}
		
		StringBuilder sb = new StringBuilder();
		
		switch (words[0]) {
		case "w": 
			for (Workout w: Workout.list(i, this.conn)) {
				sb.append(w.toString() + "\n");
			};
		break;
		
		case "x": 
			for (Exercise x: Exercise.list(i, this.conn)) {
				sb.append(x.toString() + "\n");
			};
		break;
		
		case "e": 
			for (Equipment e: Equipment.list(i, this.conn)) {
				sb.append(e.toString() + "\n");
			};
		break;
		
		case "g": 
			for (ExerciseGroup g: ExerciseGroup.list(i, this.conn)) {
				sb.append(g.toString() + "\n");
			};
		break;
		
		default: sb.append(this.error);
		}
		
		return sb.toString();
	}
	
	private void create(String input) {
		
	}
	
	private String detail(String input) {
		String[] words = input.split(" ");
		int i;
		try {
			i = Integer.parseInt(words[1]);
		} catch (IndexOutOfBoundsException | NumberFormatException e) {
			return "Eg må nok få eit tal...";
			
		}
		
		try {
			switch (words[0]) {
			case "w": return new Workout(i, this.conn).detailedString(this.conn);
			case "x": return Exercise.New(i, this.conn).detailedString();
			case "e": return new Equipment(i, this.conn).detailedString(this.conn);
			case "g": return new ExerciseGroup(i, this.conn).detailedString(this.conn);
			
			default: return this.error;
			}
		} catch (SQLException e) {
			System.out.println(e);
			return "Oops... Eg fann ikkje det du såg etter.";
		}
	}
	
	private void help() {
		try {
			System.out.println(
					this.helpMessage.get(this.state) + "\n"
					+ this.example.get(this.state));
		} catch(IndexOutOfBoundsException e) {
			System.out.println("Vil du ha hjelp?");	
		}
	}
	
	private String breakdown() {
		this.state = State.INITIAL;
		return "Det forstod eg ikkje heilt...\nDu er tilbake på startmenyen.";
	}
}