package diary;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Scanner;

import javax.swing.text.html.HTMLDocument.HTMLReader.IsindexAction;

import diary.Conversation.State;

public class Conversation {
	enum State {
		INITIAL, LIST, SEARCH, CREATE, DETAIL, REMOVE, ADD, APPEND_GROUP, APPEND_WORKOUT, WORKOUT_INTERVAL
	}

	HashMap<State, String> command = new HashMap<State, String>();
	HashMap<State, String> helpMessage = new HashMap<State, String>();
	HashMap<State, String> example = new HashMap<State, String>();

	String prompt = " > ";
	String error = "Det forstod eg ikkje heilt...";
	String entityCodes = "w = Treningsøkt\n"
			+ "e = Apparat\n"
			+ "x = Øving\n"
			+ "g = Øvingsgruppe\n";
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
				+ "addg = Registrer ei øving i ei gruppe\n"
				+ "addw = Registrer ei øving i ei treningsøkt\n\n"
				+ "Meir avanserte funksjonar:\n"
				+ "win = Sjå resultatlogg økter i eit gitt tidsintervall\n"
				+ "wog = Sjå kva for grupper som har vore representert i ei treningsøkt\n"
				+ "");
		
		this.helpMessage.put(State.LIST, "Skriv kva for element du vil sjå ei liste over, og eventuelt kor mange.\n\n" + this.entityCodes);
		this.helpMessage.put(State.DETAIL, "Skriv inn kva for type element du vil sjå, med ID.\n\n" + this.entityCodes);
		this.helpMessage.put(State.APPEND_GROUP, "Skriv inn # for øvinga, etterfullgt av # for gruppa.");
		this.helpMessage.put(State.APPEND_WORKOUT, "Skriv inn # for øvinga, etterfullgt av # for treningsøkta.");
		this.helpMessage.put(State.CREATE, "Format på kommandoar for å legge til element:"
				+ "Apparat: e [Namn] [Beskriving]"
				+ "Øving (med apparat): xe [Namn] [Beskriving]"
				+ "Øving (utan apparat): xu [Namn] [# på apparat] [Vekt] [Sett]"
				+ "Gruppe: g [Namn]"
				+ "Treningsøkt: w [Dato: yyyy-mm-dd] [Tidspunkt: hh:mm:ss] [varigheit, i minutt]\n"
				+ "[Personleg form, 1-10] [Personleg yting, 1-10] [Notat]");
		
		this.example.put(State.INITIAL, "");
		this.example.put(State.LIST, "Om du til dømes vil sjå dei tre siste treningsøktene, skriv \"w 3\".");
		this.example.put(State.DETAIL, "Om du til dømes vil sjå detaljert informasjon om treningsøkt #3, skriv \"w 3\".");
		this.example.put(State.APPEND_GROUP, "Om du vil legge øving #2 til gruppe #3, skriv du \"2 3\"");
		this.example.put(State.APPEND_WORKOUT, "Om du vil legge øving #2 til treningsøkt #3, skriv du \"2 3\"");
		this.example.put(State.CREATE, "Eksempel:\n"
				+ "Apparat: e Ergometersykkel Det er ein sånn du syklar på"
				+ "Øving (utan apparat): xu Spensthopp Hopp med mykje spenst!"
				+ "Øving (med apparat): xe Sykling 1 10 40"
				+ "Gruppe: g Beintrening"
				+ "Treningsøkt: w 2019-03-31 15:00:00 45 7 7 Helledussen, så flink eg var!");
				
		System.out.print("Velkommen til treningsdagboka di! Kva vil du gjere?");
		this.transition(State.INITIAL);
		System.out.println(this.getPrompt());
	}
	
	public String getPrompt() {
		return this.prompt;
	}
	
	public void feed(String input) throws SQLException {
		this.evaluate(input.trim());
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
		case CREATE: System.out.println(this.create(input)); break;
		case APPEND_GROUP: System.out.println(this.appendGroup(input)); break;
		case APPEND_WORKOUT: System.out.println(this.appendWorkout(input)); break;
		
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
			this.transition(State.DETAIL);
		} else if ("addg".equals(input)) {
			this.transition(State.APPEND_GROUP);
		} else if ("addw".equals(input)) {
			this.transition(State.APPEND_WORKOUT);
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
	
	private String create(String input) throws SQLException {
		String[] words = input.split(" ");
		
		try {
		switch (words[0]) {
		case "": break;
		case "g": return ExerciseGroup.insert(words[1], this.conn).detailedString(this.conn);
		case "e": return Equipment.insert(words[1], this.nthSubstring(input, 3), this.conn).detailedString(this.conn);
		case "xe": return EquippedExercise.insert(
				words[0],
				new Equipment(Integer.parseInt(words[1]), this.conn),
				Integer.parseInt(words[2]),
				Integer.parseInt(words[3]),
				this.conn).detailedString();
		case "xu": return UnequippedExercise.insert(words[1], this.nthSubstring(input, 3), this.conn).detailedString();
		case "w": return Workout.insert(
				words[1],
				words[2],
				Integer.parseInt(words[3]),
				Integer.parseInt(words[4]),
				Integer.parseInt(words[5]),
				this.nthSubstring(input, 7),
				this.conn).detailedString(this.conn);
		
		default: return this.breakdown();
		}
		} catch (IndexOutOfBoundsException | NumberFormatException | SQLException e) {
			System.out.println(e);
		}
		
		return "";
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
	
	private String appendGroup(String input) {
		try {
			String[] words = input.split(" ");
			ExerciseGroup _g = new ExerciseGroup(Integer.parseInt(words[0]), this.conn);
			_g.addExercise(Integer.parseInt(words[1]), this.conn);
			
			return _g.detailedString(this.conn);
		}
		catch (IndexOutOfBoundsException | SQLException e) {
			System.out.println(e);
			return "Det gjekk ikkje heilt. Prøv igjen!";
		}
	}
	
	private String appendWorkout(String input) {
		try {
			String[] words = input.split(" ");
			Workout _w = new Workout(Integer.parseInt(words[0]), this.conn);
			_w.addExercise(Integer.parseInt(words[1]), this.conn);
			
			return _w.detailedString(this.conn);
		}
		catch (IndexOutOfBoundsException | SQLException e) {
			System.out.println(e);
			return "Det gjekk ikkje heilt. Prøv igjen!";
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
	
	private String nthSubstring(String s, int n) {
		int currentIndex = 0;
		
		for (int i = 1; i < n; i++) {
			currentIndex = s.indexOf(' ', currentIndex + 1);
		}
		
		return s.substring(currentIndex+1);
	}
	
	public static void main(String[] args) {
		String input = "e Staurmaskin Det er sjukt kult å bere staur!";
		String manyp = "yyyy-mm-dd 15:00:00 45 7 7 Helledussen, så flink eg var!";
		
		/*
		System.out.println(input);
		System.out.println(Conversation.nthSubstring(input, 3));
		System.out.println(Conversation.nthSubstring(manyp, 6));
		//System.out.println(manyp.substring(55));*/
	}
}

abstract class Screen {
	
}

class ConnectionScreen extends Screen {
	
}

class WelcomeScreen extends Screen {
	
	
}