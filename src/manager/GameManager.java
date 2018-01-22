package manager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import device.ScemtecReader;
import entities.Player;
import http.WebServer;

public class GameManager {

	private ScemtecReader sr;
	private WebServer ws;

	private int ballCount = 0;
	private int playerCount = 0;
	private ArrayList<Player> players;
	private int STATE = 1;

	public GameManager() {
		System.out.println("GameManager initialized");
		sr = new ScemtecReader();
		ws = new WebServer(this);
		ws.startServer();
		players = new ArrayList<>();

	}

	public ArrayList<Player> getPlayers() {
		return this.players;
	}

	public int getBallCount() {
		return this.ballCount;
	}

	public void tick() throws InterruptedException {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		switch (STATE) {

		// Enter number of players
		case 1:

			System.out.print("Anzahl der Spieler eingeben:");
			try {
				playerCount = Integer.valueOf(br.readLine());
			} catch (IOException e) {
				e.printStackTrace();
			}
			STATE++;
			break;

		// Enter name of players
		case 2:
			for (int i = 0; i < playerCount; i++) {
				System.out.print("Bitte den Namen von Spieler " + (i + 1) + " eingeben: ");
				try {
					players.add(new Player(br.readLine()));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			STATE++;
			break;

		// enter ball count
		case 3:
			System.out.print("Bitte die Anzahl der Bälle pro Spieler eingeben: ");
			try {
				ballCount = Integer.valueOf(br.readLine());
			} catch (IOException e) {
				e.printStackTrace();
			}
			STATE++;
			break;

		// print information
		case 4:
			System.out.println("");
			System.out.println("-------------------------------------");
			System.out.println("Jeder Spieler hat " + ballCount + " Bälle zur Verfügung");
			System.out.println("-------------------------------------");
			STATE++;
			break;

		// register balls for all players
		case 5:

			for (Player p : players) {
				System.out.println(p.getName() + " : Halte nun alle Bälle in die Zielzone...");
				// loop until we have all balls
				while (p.getTags().size() < ballCount) {
					// check, if there is a new ball
					for (String tag : sr.getTags()) {
						if (!p.getTags().contains(tag)) {
							System.out.println("Neuer Ball gefunden: " + tag);
							// New ball found => add it
							p.getTags().add(tag);
							System.out.println("Registrierte Bälle: " + p.getTags().size() + "/" + ballCount);

						}
					}
				}

				// here we have all balls for the player
				System.out.println(p.getName() + ": Alle Bälle registriert!");
				System.out.println("-------------------------------------");
				Thread.sleep(2000);

			}
			STATE++;
			break;

		// check for new target hits
		case 6:
			// check if someone hit the target
			for (String tag : sr.getTags()) {
				for (Player p : players) {
					if (p.getTags().contains(tag)) {
						p.setScore(p.getScore() + 1);
						System.out.println(p.getName() + " hat getroffen! (Tag: " + tag + ")");
					}
				}
			}
			break;

		}
	}

	private void printStart() {
		System.out.println("RFID Game v0.1");
		System.out.println("-------------------------------------");
	}

	// loop
	public void run() {
		printStart();
		while (true) {
			try {
				tick();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

}
