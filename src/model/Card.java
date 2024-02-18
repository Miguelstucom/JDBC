package model;

import utils.Color;
import utils.Number;


public class Card {
	
	private int id;
	private Number number;
	private Color color;
	private int playerId;
	
	public Card(int id, String number, String color, int playerId) {
		this.id = id;
		this.number = Number.valueOf(number);
		this.color = Color.valueOf(color);
		this.playerId = playerId;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getNumber() {
		return number.toString();
	}

	public void setNumber(Number number) {
		this.number = number;
	}

	public String getColor() {
		return color.toString();
	}

	public void setColor(Color color) {
		this.color = color;
	}

	public int getPlayerId() {
		return playerId;
	}

	public void setPlayerId(int playerId) {
		this.playerId = playerId;
	}

	@Override
	public String toString() {
		//return "Card [id=" + id + ", number=" + number + ", color=" + color + ", playerId=" + playerId + "]";
		return "Card [number=" + number + ", color=" + color + "]";
	}

		
}
