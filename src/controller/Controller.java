package controller;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Scanner;

import dao.DaoImpl;
import model.Card;
import model.Player;
import utils.Color;

import utils.Number;

/**
 * 
 */
/**
 * 
 */
public class Controller {
	private static Controller controller;
	private DaoImpl dao;
	private Player player;
	private ArrayList<Card> cards;
	private Scanner s;
	private Card lastCard;

	private Controller () {
		dao = new DaoImpl();
		s = new Scanner(System.in);
	}
	
	public static Controller getInstance() {
		if (controller == null) {
			controller = new Controller();
		}
		return controller;
	}

	
	/**
	 * Start game,
	 * connect to db
	 * check user/pw
	 * play a card
	 */
	public void init() {
		try {
			// connect to data
			dao.connect();
			
			// if login ok 
			if (loginUser()) {
				// check last game
				startGame();
				// play turn based on cards in hand
				playTurn();
				
				
			} else
				System.out.println("User or password incorrect.");
		} catch (SQLException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		} finally {
			try {
				// disconnect data
				dao.disconnect();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Show cards in hand
	 * Ask for player action
	 * @throws SQLException
	 */
	private void playTurn() throws SQLException {
		Card card = null;
		boolean correctCard= false;
		boolean end=false;
		
		// loop until option end game option or no more cards
		while (!end) {
			// loop until selected card matches rules based on last card on table
			do {				
				showCards();				
				System.out.println("Press -1 to take a new one.");
				System.out.println("Press -2 to exit game.");
				int position=0;
				do {
					System.out.println("Select card to play.");
					position = s.nextInt();
					if (position>=cards.size()) {
						System.out.println("Card does not exist in your hand, try again.");
					}
				} while (position>=cards.size());
								
				switch (position) {
				case -2:
					correctCard = true;
					end = true;
					System.out.println("Exiting game by pressing EXIT OPTION");
					break;

				case -1:
					drawCards(1);
					break;

				default:
					card = selectCard(position);
					correctCard = validateCard(card);

					// if skip or change side, remove it and finish game
					if (correctCard) {
						if (card.getNumber().equalsIgnoreCase(Number.SKIP.toString())
								|| card.getNumber().equalsIgnoreCase(Number.CHANGESIDE.toString())) {
							// remove from hand
							this.cards.remove(card);
							dao.deleteCard(card);
							// to end game
							end = true;
							System.out.println("Exiting game by EXIT CARD");
							break;
						}
					}

					// if correct card and no exit card
					if (correctCard && !end) {
						System.out.println("Well done, next turn");
						lastCard = card;
						// save card in game data
						dao.saveGame(card);
						// remove from hand
						this.cards.remove(card);

					} else {
						System.out.println("This card does not match the rules, try other card or draw the deck");
					}
					break;
				}
			} while (!correctCard);
			
			// if no more cards, ends game
			if (this.cards.size() == 0) {
				endGame();
				end=true;
				System.out.println("Exiting game, no more cards, you win.");
				break;
			}			
		}		
	}

	/**
	 * @param card to be played
	 * @return true if it is right based on last card
	 */
	private boolean validateCard(Card card) {
		if (lastCard != null) {
			// same color than previous one
			if (lastCard.getColor().equalsIgnoreCase(card.getColor())) return true;
			// same number than previous one
			if (lastCard.getNumber().equalsIgnoreCase(card.getNumber())) return true;
			// last card is black, it does not matter color
			if (lastCard.getColor().equalsIgnoreCase(Color.BLACK.name())) return true;
			// current card is black, it does not matter color
			if (card.getColor().equalsIgnoreCase(Color.BLACK.name())) return true;
			
			return false;
		} else {
			return true;
		}
	}

	/**
	 * add a new win game
	 * add a new played game
	 * @throws SQLException
	 */
	private void endGame() throws SQLException {
		dao.addVictories(player.getId());
		dao.addGames(player.getId());
		dao.clearDeck(player.getId());
	}

	private Card selectCard(int id) {
		Card card = this.cards.get(id);
		return card;
	}

	private void showCards() {
		System.out.println("================================================");
		if (null == lastCard) {
			System.out.println("First time playing, no cards on table");
		} else {
			System.out.println("Card on table is " + lastCard.toString());
		}
		System.out.println("================================================");
		System.out.println("Your " + cards.size() + " cards in your hand are ...");
		for (int i = 0; i < cards.size(); i++) {
			System.out.println(i + "." + cards.get(i).toString());
		}
	}

	/**
	 * @return true if user/pw found
	 * @throws SQLException
	 */
	private boolean loginUser() throws SQLException {
		System.out.println("Welcome to UNO game!!");
		System.out.println("Name of the user: ");
		String user = s.next();
		System.out.println("Password: ");
		String pass = s.next();
		
		player = dao.getPlayer(user, pass);
		
		if (player != null) {
			return true;
		}
		return false;		
	}
	
	
	/**	 
	 * @throws SQLException
	 */
	private void startGame() throws SQLException {
		// get last cards of player
		cards = dao.getCards(player.getId());
		
		// if no cards, first game, take 3 cards
		if (cards.size() == 0) drawCards(3);
		
		// get last played card
		lastCard = dao.getLastCard();
		
		// for last card +2, take two more
		if (lastCard != null && lastCard.getNumber().equalsIgnoreCase(Number.TWOMORE.toString())) drawCards(2);
		// for last card +4, take four more
		if (lastCard != null && lastCard.getNumber().equalsIgnoreCase(Number.FOURMORE.toString())) drawCards(4);
	}
	
	
	/**
	 * get a number of cards from deck adding them to hand of player
	 * @param numberCards
	 * @throws SQLException
	 */
	private void drawCards(int numberCards) throws SQLException {
		
		for (int i = 0; i < numberCards; i++) {
			int id = dao.getLastIdCard(player.getId());
			
			// handle depends on number color must be black or random
			String number = Number.getRandomCard();
			String color="";
			if (number.equalsIgnoreCase(Number.WILD.toString())|| number.equalsIgnoreCase(Number.FOURMORE.toString())){
				color = Color.BLACK.toString();
			}else {
				color = Color.getRandomColor();
			}
					
			Card c = new Card(id, number , color , player.getId());
			dao.saveCard(c);
			cards.add(c);
		}
	}


}
