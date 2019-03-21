package diary;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.HashMap;
import java.util.Scanner;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import diary.Conversation.State;

public class Conversation {
	enum State {
		INITIAL, LIST, SEARCH, CREATE, DETAIL, REMOVE, ADD, APPEND_GROUP, APPEND_WORKOUT, WORKOUT_INTERVAL
	}

	HashMap<State, String> command = new HashMap<State, String>();
	HashMap<State, String> helpMessage = new HashMap<State, String>();
	HashMap<State, String> example = new HashMap<State, String>();

	Screen screen;
	ConnectionScreen connectionScreen;
	
	boolean connected;
	boolean done = false;
	String prompt = " > ";
	String error = "Det forstod eg ikkje heilt...";
	String entityCodes = "w = Treningsøkt\n"
			+ "e = Apparat\n"
			+ "x = Øving\n"
			+ "g = Øvingsgruppe\n";
	State state;
	State prevState;
	DBConn conn;
	
	Conversation() {
		this.connected = false;
		//this.screen = new ConnectionScreen();

		// BERRE FOR TESTING
		try {
			this.conn = new DBConn("localhost", "Diary", "root", "fish");
		} catch (SQLException | ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(1);
		}
		
		this.connected = true;
		this.screen = new WelcomeScreen();
		// SLUTT PÅ TESTING		
		//this.conn = conn;
		System.out.print(this.getPrompt());
	}
	
	public String getPrompt() {
		return this.prompt;
	}
	
	public void feed(String input) throws SQLException {
		if (this.screen.isDone()) {
			switch (this.screen.getClass().getName()) {
			case "diary.ConnectionScreen":
				this.conn = ((ConnectionScreen) this.screen).getConnection();
				this.connected = true;
				this.screen = new WelcomeScreen();
			break;
			case "diary.WelcomeScreen":
				switch (((WelcomeScreen) this.screen).getExitCode()) {
				case 0: this.done = true; break;
				case 1: this.screen = new EquipmentScreen(this.conn); break;
				case 2: this.screen = new ExerciseScreen(this.conn); break;
				case 3: this.screen = new GroupScreen(this.conn); break;
				case 4: this.screen = new WorkoutScreen(this.conn); break;
				}
			return;
			
			default:
				this.screen = new WelcomeScreen();
			}
		}
		
		this.screen.evaluate(input);
	}
	
	private void evaluate(String input) {
		
	}
	
	public boolean isDone() {
		return this.done;
	}
	

	
	public static void main(String[] args) throws ClassNotFoundException, SQLException {
		//String input = "e Staurmaskin Det er sjukt kult å bere staur!";
		//String manyp = "yyyy-mm-dd 15:00:00 45 7 7 Helledussen, så flink eg var!";
				
		Scanner scan = new Scanner(System.in);
		Conversation c = new Conversation();
		String input;		
		while (!c.isDone() && scan.hasNext()) {
			input = scan.nextLine();
			//System.out.println(input);
			c.feed(input);
			System.out.print(" > ");
		}
		System.out.println("Velkommen tilbake!");
		scan.close();
	}
	
}

abstract class Screen {
	protected boolean done = false;
	
	public abstract void evaluate(String input);
	
	public boolean isDone() {
		return this.done;
	}
	public static int wordToInt(String[] word, int index) {
		try {
			return Integer.parseInt(word[index]);
		} catch (IndexOutOfBoundsException | NumberFormatException e) {
			return 0;
		}
	}
}

class CreationScreen extends Screen {
	CreationScreen() {
		
	}
	
	public void evaluate(String inpunt) {
		
	}
}

class ConnectionScreen extends Screen {
	private DBConn conn;
	private boolean done = false;
	private int n;
	private String[] word = {"", "", "", ""};
	private String[] wordLabel = {"server", "database", "brukarnamn", "passord"};
	
	ConnectionScreen() {
		this.n = 0;
		System.out.println("Hei.\nSkriv inn " + this.wordLabel[n] + ": ");
	}
	
	public void evaluate(String input) {
		this.word[n] = input;
		this.n += 1;
		
		if (n > 3) {
			try {
				this.conn = new DBConn(this.word[0], this.word[1], this.word[2], this.word[3]);
				this.done = true;
			} catch (SQLException | ClassNotFoundException e) {
				e.printStackTrace();
				this.n = 0;
			}
			
			return;
		}
		
		System.out.println("Skriv inn " + this.wordLabel[n] + ": ");
	}
	
	public DBConn getConnection() {
		return this.conn;
	}
	
	public boolean isDone() {
		return this.done;
	}
}

class WelcomeScreen extends Screen {
	private int exitCode;
	private String helpMessage = "Kva vil du gjere? Skriv inn ein av følgande kommandoar:\n"
			+ "a = administrere apparat\n"
			+ "o = administrere øvingar\n"
			+ "g = administrere øvingsgrupper\n"
			+ "t = administrere treningsøkter\n\n"
			+ "help = sjå denne tekste på nytt\n"
			+ "tbak = avslutt programmet";
	
	WelcomeScreen() {
		System.out.println("Velkommen til treningsdagboka di!\n" + this.helpMessage);
	}
	public boolean isDone() {
		return this.done;
	}

	public int getExitCode() {
		return this.exitCode;
	}
	
	@Override
	public void evaluate(String input) {
		switch (input.trim()) {
		case "a": this.exitCode = 1; this.done = true; break;
		case "o": this.exitCode = 2; this.done = true; break;
		case "g": this.exitCode = 3; this.done = true; break;
		case "t": this.exitCode = 4; this.done = true; break;
		case "help": System.out.println(this.helpMessage); break;
		case "tbak": this.exitCode = 0; this.done = true; break;
		
		default: System.out.println("No forstår eg ikkje heilt kva du meiner..."); break;
		}
	}
}

class EntityScreen extends Screen {
	protected DBConn conn;
	protected CreationScreen creationScreen;
	protected enum State {
		INITIAL, CREATE
	};
	
	String[] helpMessage;
	
	@Override
	public boolean isDone() {
		// TODO Auto-generated method stub
		return this.done;
	}

	@Override
	public void evaluate(String input) {
		// TODO Auto-generated method stub
		
	}
	
	public static <T> String listStrings(Collection<T> col) {
		return col.stream()
		.map(n -> n.toString())
		.collect(Collectors.joining("\n"));
	}
	
	public String yolo(String[] word, Supplier<DiaryEntity> func) {
		return "";
	}
}

class EquipmentScreen extends EntityScreen {
	DBConn conn;
	String[] helpMessage = {
			"Tilgjengelege kommandoar:\n"
			+ "list = Sjå ei liste over apparat.\n"
			+ "info = Sjå detaljert informasjon om eit apparat.\n"
			+ "nytt = Registrer eit nytt apparat.\n"
			+ "help = Få hjelp til å administrere apparat.\n"
			+ "tbak = Gå tilbake til velkomstskjermen\n",
			"Eksempel på kommandoar:\n"
			+ "Skriv \"list\" om du vil sjå ei liste over alle apparata."
			+ "Skriv \"list 2\" om du vil sjå to apparat.\n"
			+ "Skriv \"info 2\" om du vil sjå detaljert informasjon om apparat #2.\n"
			+ "Skriv \"nytt\" om du vil registrere eit nytt apparat. Du vil deretter bli spurd om meir info.\n",
	};
	
	String[] inputWord = {"", ""};
	String[] inputLabel = {"namn", "beskriving"};
	String[] inputPattern = {"", ""};
	
	int n;
	State state;
	
	EquipmentScreen(DBConn conn) {
		this.conn = conn;
		this.state = State.INITIAL;
		System.out.println(this.helpMessage[0]);
		System.out.print("\n > ");
	}

	public void evaluate(String input) {
		switch (this.state) {
		case CREATE: this.create(input); break;
		
		default: this.splash(input); break;
		}
	}
	
	private void splash(String input) {
		String[] word = input.split(" ");
		
		try {
			switch (word[0]) {
			case "tbak": this.done = true; System.out.println(this.isDone()); break;
			case "help": System.out.println(this.helpMessage[0] + "\n" + this.helpMessage[1]); break;
			case "list": System.out.println(this.list(word)); break;
			case "info": System.out.println(this.detail(word)); break;
			case "nytt": this.prepareCreation(); break;
			
			default: System.out.println("No forstår eg ikkje heilt kva du meiner..."); break;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}		
	}
	
	public void prepareCreation() {
		this.n = 0; 
		this.state = State.CREATE;
		
		System.out.println("Skriv inn " + this.inputLabel[n] + ": "); 
	}
	
	public void create(String input) {
		this.inputWord[n] = input;
		
		if (true) {
			this.n += 1;
		}
		
		if (n > 1) {
			try {
				System.out.println(Equipment.insert(inputWord[0], inputWord[1], this.conn));
			} catch (SQLException e) {
				e.printStackTrace();
				this.n = 0;
			}
			
			for (int i = 0; i < this.inputWord.length; i++) {
				this.inputWord[i] = "";
			}
			
			this.state = State.INITIAL;
			return;
		}
		
		System.out.println("Skriv inn " + this.inputLabel[n] + ": ");
	}
	
	public String detail(String[] word) {
		int i = Screen.wordToInt(word, 1);
		
		if (i <= 0) {
			return "Eg treng eit heiltal som er høgare enn 0.";
		}
		
		try {
			return new Equipment(i, this.conn).detailedString(conn);
		} catch (SQLException e) {
			e.printStackTrace();
			return "Oops... Det elementet fann vi ikkje.";
		}
	}
	
	public String list(String[] word) throws SQLException {
		return Equipment.list(Screen.wordToInt(word, 1), this.conn).stream()
				.map(n -> n.toString())
				.collect(Collectors.joining("\n"));
	}
}

class ExerciseScreen extends EntityScreen {
	DBConn conn;
	String[] helpMessage = {
			"Tilgjengelege kommandoar:\n"
			+ "list = Sjå ei liste over øvingar.\n"
			+ "info = Sjå detaljert informasjon om ei øving.\n"
			+ "nytt = Registrer ei ny øving.\n"
			+ "help = Få hjelp til å administrere øvingar.\n"
			+ "tbak = Gå tilbake til velkomstskjermen\n",
			"Eksempel på kommandoar:\n"
			+ "Skriv \"list\" om du vil sjå ei liste over alle øvingane."
			+ "Skriv \"list 2\" om du vil sjå to øvingar.\n"
			+ "Skriv \"info 2\" om du vil sjå detaljert informasjon om øving #2.\n"
			+ "Skriv \"nytt\" om du vil registrere ei ny øving. Du vil deretter bli spurd om meir info.\n",
	};
	
	String[] inputWordE = {"", "", "", ""};
	String[] inputLabelE = {"namn", "apparat-ID", "vekt", "sett"};
	String[] inputPatternE = {"", "", "", ""};

	String[] inputWordU = {"", ""};
	String[] inputLabelU = {"namn", "beskriving"};
	String[] inputPatternU = {"", ""};
	
	int n;
	State state;
	enum State {
		INITIAL, CREATE_U, CREATE_E
	};
	
	ExerciseScreen(DBConn conn) {
		this.conn = conn;
		this.state = State.INITIAL;
		System.out.println(this.helpMessage[0]);
		System.out.print("\n > ");
	}

	public void evaluate(String input) {
		switch (this.state) {
		case CREATE_U: this.create(input); break;
		case CREATE_E: this.create(input); break;
		
		default: this.splash(input); break;
		}
	}
	
	private void splash(String input) {
		String[] word = input.split(" ");
		
		try {
			switch (word[0]) {
			case "tbak": this.done = true; System.out.println(this.isDone()); break;
			case "help": System.out.println(this.helpMessage[0] + "\n" + this.helpMessage[1]); break;
			case "list": System.out.println(this.list(word)); break;
			case "info": System.out.println(this.detail(word)); break;
			case "nytt": this.prepareCreation(); break;
			
			default: System.out.println("No forstår eg ikkje heilt kva du meiner..."); break;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}		
	}
	
	public void prepareCreation() {
		this.n = 0; 
		this.state = State.CREATE_U;
		
		System.out.println("Skriv inn " + this.inputLabelU[n] + ": "); 
	}
	
	public void create(String input) {
		
	}
	
	public void createEquipped(String input) {
		
	}
	
	public void createUnequipped(String input) {
		this.inputWordU[n] = input;
		
		if (true) {
			this.n += 1;
		}
		
		if (n > 1) {
			try {
				System.out.println(Equipment.insert(inputWordU[0], inputWordU[1], this.conn));
			} catch (SQLException e) {
				e.printStackTrace();
				this.n = 0;
			}
			
			for (int i = 0; i < this.inputWordU.length; i++) {
				this.inputWordU[i] = "";
			}
			
			this.state = State.INITIAL;
			return;
		}
		
		System.out.println("Skriv inn " + this.inputLabelU[n] + ": ");
	}
	
	public String detail(String[] word) {
		int i = Screen.wordToInt(word, 1);
		
		if (i <= 0) {
			return "Eg treng eit heiltal som er høgare enn 0.";
		}
		
		try {
			return Exercise.New(i, this.conn).detailedString(conn);
		} catch (SQLException e) {
			e.printStackTrace();
			return "Oops... Det elementet fann vi ikkje.";
		}
	}
	
	public String list(String[] word) throws SQLException {
		return Exercise.list(Screen.wordToInt(word, 1), this.conn).stream()
				.map(n -> n.toString())
				.collect(Collectors.joining("\n"));
	}
}

class GroupScreen extends EntityScreen {
	DBConn conn;
	String[] helpMessage = {
			"Tilgjengelege kommandoar:\n"
			+ "list = Sjå ei liste over grupper.\n"
			+ "info = Sjå detaljert informasjon om ei gruppe.\n"
			+ "nytt = Registrer ei ny gruppe.\n"
			+ "help = Få hjelp til å administrere grupper.\n"
			+ "tbak = Gå tilbake til velkomstskjermen\n",
			"Eksempel på kommandoar:\n"
			+ "Skriv \"list\" om du vil sjå ei liste over alle gruppene."
			+ "Skriv \"list 2\" om du vil sjå to grupper.\n"
			+ "Skriv \"info 2\" om du vil sjå detaljert informasjon om gruppe #2.\n"
			+ "Skriv \"nytt\" om du vil registrere ei ny gruppe. Du vil deretter bli spurd om meir info.\n",
	};
	
	int n;
	State state;
	
	GroupScreen(DBConn conn) {
		this.conn = conn;
		this.state = State.INITIAL;
		System.out.println(this.helpMessage[0]);
		System.out.print("\n > ");
	}

	public void evaluate(String input) {
		switch (this.state) {
		case CREATE: this.create(input); break;
		
		default: this.splash(input); break;
		}
	}
	
	private void splash(String input) {
		String[] word = input.split(" ");
		
		try {
			switch (word[0]) {
			case "tbak": this.done = true; System.out.println(this.isDone()); break;
			case "help": System.out.println(this.helpMessage[0] + "\n" + this.helpMessage[1]); break;
			case "list": System.out.println(this.list(word)); break;
			case "info": System.out.println(this.detail(word)); break;
			case "nytt": this.prepareCreation(); break;
			
			default: System.out.println("No forstår eg ikkje heilt kva du meiner..."); break;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}		
	}
	
	public void prepareCreation() {
		this.n = 0; 
		this.state = State.CREATE;
		
		System.out.println("Skriv inn namn: "); 
	}
	
	public void create(String input) {
		try {
			System.out.println(ExerciseGroup.insert(input, this.conn));
		} catch (SQLException e) {
			e.printStackTrace();
			this.n = 0;
		}
		
		this.state = State.INITIAL;
	}
	
	public String detail(String[] word) {
		int i = Screen.wordToInt(word, 1);
		
		if (i <= 0) {
			return "Eg treng eit heiltal som er høgare enn 0.";
		}
		
		try {
			return new ExerciseGroup(i, this.conn).detailedString(conn);
		} catch (SQLException e) {
			e.printStackTrace();
			return "Oops... Det elementet fann vi ikkje.";
		}
	}
	
	public void append(String[] word) {
		int groupID = Screen.wordToInt(word, 1);
		int exerciseID = Screen.wordToInt(word, 2);
		
		if (groupID == 0 || exerciseID == 0) {
			System.out.println("Beggje tala må vere heiltal som er høgare enn 0.");
		}
		
		try {
			ExerciseGroup _g = new ExerciseGroup(groupID, this.conn);
			_g.addExercise(exerciseID, this.conn);
			
			System.out.println(this.list(word));
			
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Oops... Det elementet fann vi ikkje.");
		}
	}
	
	public String list(String[] word) throws SQLException {
		return ExerciseGroup.list(Screen.wordToInt(word, 1), this.conn).stream()
				.map(n -> n.toString())
				.collect(Collectors.joining("\n"));
	}
}

class WorkoutScreen extends EntityScreen {
	DBConn conn;
	String[] helpMessage = {
			"Tilgjengelege kommandoar:\n"
			+ "list = Sjå ei liste over treningsøkter.\n"
			+ "info = Sjå detaljert informasjon om ei treningsøkt.\n"
			+ "nytt = Registrer ei ny treningsøkt.\n"
			+ "intr = Sjå form og prestasjon på økter i eit gitt tidsintervall.\n"
			+ "legg = Registrer ei øving som del av ei treningsøkt.\n"
			+ "help = Få hjelp til å administrere treningsøkter.\n"
			+ "tbak = Gå tilbake til velkomstskjermen\n",
			"Eksempel på kommandoar:\n"
			+ "Skriv \"list\" om du vil sjå ei liste over alle treningsøktene."
			+ "Skriv \"list 2\" om du vil sjå to treningsøkter.\n"
			+ "Skriv \"info 2\" om du vil sjå detaljert informasjon om treningsøkt #2.\n"
			+ "Skriv \"intr " + Calendar.getInstance().get(Calendar.YEAR) + "-01-01 " + Calendar.getInstance().get(Calendar.YEAR) + "-05-01 om du vil sjå resultat frå treningane mellom 1. januar og 1. mai.\n"
			+ "Skriv \"nytt\" om du vil registrere ei nytt treningsøkt. Du vil deretter bli spurd om meir info.\n",
	};
	
	String[] inputWord = {"", "", "", "", "", ""};
	String[] inputLabel = {
			"dato",
			"tidspunkt",
			"varigheit",
			"personleg form",
			"personleg prestasjon",
			"notat"};
	String[] inputPattern = {"", ""};
	
	int n;
	State state;
	
	WorkoutScreen(DBConn conn) {
		this.conn = conn;
		this.state = State.INITIAL;
		System.out.println(this.helpMessage[0]);
		System.out.print("\n > ");
	}

	public void evaluate(String input) {
		switch (this.state) {
		case CREATE: this.create(input); break;
		
		default: this.splash(input); break;
		}
	}
	
	private void splash(String input) {
		String[] word = input.split(" ");
		
		try {
			switch (word[0]) {
			case "tbak": this.done = true; System.out.println(this.isDone()); break;
			case "help": System.out.println(this.helpMessage[0] + "\n" + this.helpMessage[1]); break;
			case "list": System.out.println(this.list(word)); break;
			case "info": System.out.println(this.detail(word)); break;
			case "intr": System.out.println(this.interval(word)); break;
			case "legg": System.out.println(this.append(word)); break;
			case "nytt": this.prepareCreation(); break;
			
			default: System.out.println("No forstår eg ikkje heilt kva du meiner..."); break;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}		
	}
	
	public void prepareCreation() {
		this.n = 0; 
		this.state = State.CREATE;
		
		System.out.println("Skriv inn " + this.inputLabel[n] + ": "); 
	}
	
	public void create(String input) {
		this.inputWord[n] = input;
		
		if (true) {
			this.n += 1;
		}
		
		if (n > 5) {
			try {
				System.out.println(Workout.insert(
						inputWord[0],
						inputWord[1],
						Integer.parseInt(inputWord[2]),
						Integer.parseInt(inputWord[3]),
						Integer.parseInt(inputWord[4]),
						inputWord[5],
						this.conn).detailedString(this.conn));
			} catch (SQLException e) {
				e.printStackTrace();
				this.n = 0;
			}
			
			for (int i = 0; i < this.inputWord.length; i++) {
				this.inputWord[i] = "";
			}
			
			this.state = State.INITIAL;
			return;
		}
		
		System.out.println("Skriv inn " + this.inputLabel[n] + ": ");
	}
	
	public String detail(String[] word) {
		int i = Screen.wordToInt(word, 1);
		
		if (i <= 0) {
			return "Eg treng eit heiltal som er høgare enn 0.";
		}
		
		try {
			return new Workout(i, this.conn).detailedString(conn);
		} catch (SQLException e) {
			e.printStackTrace();
			return "Oops... Det elementet fann vi ikkje.";
		}
	}
	
	private String interval(String[] word) {
		try {
			return Workout.getInterval(word[1], word[2], this.conn);
		} catch (IndexOutOfBoundsException e) {
			e.printStackTrace();
			return "Hugs å skrive både dato for start og slutt på formatet yyyy-mm-dd!";
		}
	}
	
	public String append(String[] word) {
		int workoutID = Screen.wordToInt(word, 1);
		int exerciseID = Screen.wordToInt(word, 2);
		
		if (exerciseID == 0 || workoutID == 0) {
			System.out.println("Beggje tala må vere heiltal som er høgare enn 0.");
		}
		
		try {
			Workout _w = new Workout(workoutID, this.conn);
			_w.addExercise(exerciseID, this.conn);
			
			return this.detail(word);
			
		} catch (SQLException e) {
			e.printStackTrace();
			return "Oops... Det elementet fann vi ikkje.";
		}
	}
	
	public String list(String[] word) throws SQLException {
		return Workout.list(Screen.wordToInt(word, 1), this.conn).stream()
				.map(n -> n.toString())
				.collect(Collectors.joining("\n"));
	}
}