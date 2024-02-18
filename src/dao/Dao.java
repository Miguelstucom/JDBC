package dao;

import java.sql.SQLException;
import java.util.ArrayList;

import model.Card;
import model.Player;

public interface Dao {
	
	/**
	 * connect to database using url, user and password
	 * @throws SQLException
	 */
	public void connect() throws SQLException;
	
	/**
	 * disconnect from database using connection close
	 * @throws SQLException
	 */
	public void disconnect() throws SQLException;
	
	/**
	 * get last id card +1 from card table by player id using ifnull(max(id),0)+1
	 * @param playerId
	 * @return lastId +1 to create a new card
	 * @throws SQLException
	 */
	public int getLastIdCard(int playerId) throws SQLException;
	
	/**
	 * get object last Card played from game join card table
	 * @return last card played 
	 * @throws SQLException
	 */
	public Card getLastCard() throws SQLException;
	
	/**
	 * get object Player by user and password from player table
	 * @param user
	 * @param pass
	 * @return
	 * @throws SQLException
	 */
	public Player getPlayer(String user, String pass) throws SQLException;	
	
	/**
	 * @param id player
	 * @return list of hand cards, they are in card table but they are not in game table(played)
	 * @throws SQLException
	 */
	public ArrayList<Card> getCards(int playerId) throws SQLException;
	
	/**
	 * @param id card
	 * @return object card from card table by id_card
	 * @throws SQLException
	 */
	public Card getCard(int cardId) throws SQLException;
	
	/**
	 * insert new game with cardId
	 * @param card
	 * @throws SQLException
	 */
	public void saveGame(Card card) throws SQLException;
	
	/**
	 * insert new card with card fields
	 * @param card
	 * @throws SQLException
	 */
	public void saveCard(Card card) throws SQLException;
	
	/**
	 * delete last card from game table if it was a end game card(change side or skip)
	 * @param card
	 * @throws SQLException
	 */
	public void deleteCard(Card card) throws SQLException;
	
	/**
	 * delete all records from card and game tables
	 * @throws SQLException
	 */
	public void clearDeck(int playerId) throws SQLException;
	
	/**
	 * update victories field if the game ends successfully using player id
	 * @param playerId
	 * @throws SQLException
	 */
	public void addVictories(int playerId) throws SQLException;
	
	/**
	 * update games field if the game ends using player id
	 * @param playerId
	 * @throws SQLException
	 */
	public void addGames(int playerId) throws SQLException;

}
