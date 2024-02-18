package utils;

import java.util.Random;

public enum Number {
	ZERO,ONE,TWO,THREE,FOUR,FIVE,SIX,SEVEN,EIGHT,NINE,CHANGESIDE,TWOMORE, SKIP,WILD,FOURMORE;
	
	public static String getRandomCard() {
		Random r = new Random();
		switch (r.nextInt(14)+1) {
			case 1: return ONE.toString();
			case 2: return TWO.toString();
			case 3: return THREE.toString();
			case 4: return FOUR.toString();
			case 5: return FIVE.toString();
			case 6: return SIX.toString();
			case 7: return SEVEN.toString();
			case 8: return EIGHT.toString();
			case 9: return NINE.toString();
			case 10: return CHANGESIDE.toString();
			case 11: return TWOMORE.toString();
			case 12: return SKIP.toString();
			case 13: return WILD.toString();
			default: return FOURMORE.toString();
		}
	}
}
