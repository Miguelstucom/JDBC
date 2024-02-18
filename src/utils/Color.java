package utils;

import java.util.Random;

public enum Color {
	RED, YELLOW, GREEN, BLUE, BLACK;
	
	public static String getRandomColor() {
		Random r = new Random();
		switch (r.nextInt(4)+1) {
			case 1: return RED.toString();
			case 2: return YELLOW.toString();
			case 3: return GREEN.toString();
			default: return BLUE.toString();
		}
	}
}
