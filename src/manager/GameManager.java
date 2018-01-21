package manager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import device.ScemtecReader;

public class GameManager {

	private ScemtecReader sr;
	private String player1 = "";
	private String player2 = "";
	private int ballCount = 0;
	private int scoreP1 = 0;
	private int scoreP2 = 0;

	private int STATE = 0;
	private ArrayList<String> tags;
	private ArrayList<String> Player1Tags;
	private ArrayList<String> Player2Tags;

	public GameManager() {
		System.out.println("GameManager initialized");
		sr = new ScemtecReader();
		Player1Tags = new ArrayList<>();
		Player2Tags = new ArrayList<>();
	}

	public void tick() throws InterruptedException {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		switch (STATE) {
		// Enter name of player 1
		case 0:

			System.out.print("Bitte den Namen von Spieler 1 eingeben: ");
			try {
				player1 = br.readLine();
			} catch (IOException e) {
				e.printStackTrace();
			}
			STATE++;
			break;

		// Enter name of player 2
		case 1:

			System.out.print("Bitte den Namen von Spieler 2 eingeben: ");
			try {
				player2 = br.readLine();
			} catch (IOException e) {
				e.printStackTrace();
			}
			STATE++;
			break;

		// enter ball count
		case 2:
			System.out.print("Bitte die Anzahl der Bälle pro Spieler eingeben: ");
			try {
				ballCount = Integer.valueOf(br.readLine());
			} catch (IOException e) {
				e.printStackTrace();
			}
			STATE++;
			break;

		// print information
		case 3:
			System.out.println("");
			System.out.println("-------------------------------------");
			System.out.println("Spieler: " + this.player1 + " & " + this.player2);
			System.out.println("Jeder Spieler hat " + ballCount + " Bälle zur Verfügung");
			System.out.println("-------------------------------------");
			System.out.println("Spieler 1: Halte nun alle Bälle in die Zielzone...");
			STATE++;
			break;

		// register balls for player 1
		case 4:
			tags = sr.getTags();
			// check, if there is a new ball
			for (String tag : tags) {
				if (!Player1Tags.contains(tag)) {
					System.out.println("Neuer Ball gefunden: " + tag);
					// New ball found => add it
					Player1Tags.add(tag);
					// check if we have all balls
					if (Player1Tags.size() == ballCount) {
						System.out.println("Spieler 1: Alle Bälle registriert!");
						System.out.println("-------------------------------------");
						Thread.sleep(2000);
						System.out.println("Spieler 2: Halte nun alle Bälle in die Zielzone...");
						STATE++;
					}
				}
			}

			break;

		// register balls for player 2
		case 5:
			tags = sr.getTags();
			// check, if there is a new ball
			for (String tag : tags) {
				if (!Player2Tags.contains(tag)) {
					System.out.println("Neuer Ball gefunden: " + tag);
					// New ball found => add it
					Player2Tags.add(tag);
					// check if we have all balls
					if (Player2Tags.size() == ballCount) {
						System.out.println("Spieler 2: Alle Bälle registriert!");
						System.out.println("-------------------------------------");
						Thread.sleep(2000);
						System.out.println(
								"Das Spiel beginnt! Jeder kann nun die Bälle werfen (egal wann, auch gleichzeitig möglich)...");
						STATE++;
					}
				}
			}
			break;
		// check for new target hits
		case 6:
			tags = sr.getTags();
			// check if someone hit the target
			for (String tag : tags) {
				// check if the tag belongs to player1
				if (Player1Tags.contains(tag)) {
					System.out.println("Spieler 1 hat getroffen!");
					scoreP1++;
					// check if tag belongs to player2
				} else if (Player2Tags.contains(tag)) {
					System.out.println("Spieler 2 hat getroffen!");
					scoreP2++;
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
