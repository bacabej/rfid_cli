package entities;

import java.util.ArrayList;

/**
 * 
 * @author Sebastian Düringer, Hamza Ammar
 * @version 0.1
 * 
 *          The class Player stores information about a player like name, score
 *          and a list of all tags registered to this player
 */
public class Player {

	private String name;
	private int score;
	private ArrayList<String> tags = new ArrayList<>();

	public Player(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}

	public ArrayList<String> getTags() {
		return tags;
	}

	public void setTags(ArrayList<String> tags) {
		this.tags = tags;
	}
}
