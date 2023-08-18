package brodici;

public class Message {
	String event;
	Integer row;
	Integer col;
	Boolean hit;
	Boolean sink;
	Ship ship;
	Boolean gameOver;
	Integer turns;
	String ip, name;

	Message() {

	}

	Message(String string) {
		this.event = string;
	}

	Message(Integer row, Integer col) {
		event = "shoot";
		this.row = row;
		this.col = col;
	}

	Message(Boolean hit, Integer row, Integer col, Boolean sink, Ship ship, Boolean gameOver) {
		event = "odgovor";
		this.hit = hit;
		this.row = row;
		this.col = col;
		this.sink = sink;
		this.ship = ship;
		this.gameOver = gameOver;
	}

	Message(Integer turns, String ip) {
		event = "game over";
		this.turns = turns;
		this.ip = ip;
	}
	
	Message(String name, Integer broj) {
		this.event = "name";
		this.name = name; 
	}

}
