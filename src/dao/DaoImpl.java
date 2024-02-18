package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import model.Card;
import model.Player;

public class DaoImpl implements Dao {
	
	private Connection conn;

	@Override
	public void connect() throws SQLException {
        final String URL = "jdbc:mysql://localhost:3306/uno";
        final String USER = "root";
        final String PASSWORD = "";
        try {
            conn = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Conexión realizada con éxito");
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
	}

	@Override
	public void disconnect() throws SQLException {
        if (conn != null && !conn.isClosed()) {
            conn.close();
            System.out.println("Desconexión realizada con éxito");
        }

	}

	@Override
	public int getLastIdCard(int playerId) throws SQLException {
        int lastIdCard = 0;

        String query = "SELECT MAX(id) FROM card WHERE id_player = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, playerId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    lastIdCard = rs.getInt(1);
                }
            }
        }

        return lastIdCard;
    }
	

	@Override
	public Card getLastCard() throws SQLException {
	    Card lastCard = null;
	    String query = "SELECT id_card FROM game WHERE id = (SELECT MAX(id) FROM game)";

	    try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
	        if (rs.next()) {
	            int cardId = rs.getInt("id_card");

	            String cardDetailsQuery = "SELECT * FROM card WHERE id = ?";
	            try (PreparedStatement cardStmt = conn.prepareStatement(cardDetailsQuery)) {
	                cardStmt.setInt(1, cardId);
	                ResultSet cardRs = cardStmt.executeQuery();

	                if (cardRs.next()) {
	                    int id = cardRs.getInt("id");
	                    String number = cardRs.getString("number");
	                    String color = cardRs.getString("color");
	                    int playerId = cardRs.getInt("id_player");
	                    lastCard = new Card(id, number, color, playerId);
	                }
	            }
	        }
	    }

	    return lastCard;
	}


	@Override
    public Player getPlayer(String user, String pass) throws SQLException {
        Player player = null;

        String query = "SELECT * FROM player WHERE user = ? AND password = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, user);
            stmt.setString(2, pass);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int id = rs.getInt("id");
                    String name = rs.getString("name");
                    int games = rs.getInt("games");
                    int victories = rs.getInt("victories");
                    player = new Player(id, name, games, victories);
                }
            }
        }

        return player;
    }

	@Override
	public ArrayList<Card> getCards(int playerId) throws SQLException {
	    ArrayList<Card> cards = new ArrayList<>();

	    String query = "SELECT * FROM card C LEFT JOIN game G ON C.id = G.id_card WHERE id_player = ? AND G.id IS NULL";
	    try (PreparedStatement stmt = conn.prepareStatement(query)) {
	        stmt.setInt(1, playerId);
	        try (ResultSet rs = stmt.executeQuery()) {
	            while (rs.next()) {
	                int id = rs.getInt("id");
	                String number = rs.getString("number");
	                String color = rs.getString("color");
	                cards.add(new Card(id, number, color, playerId));
	            }
	        }
	    }

	    return cards;
	}


	@Override
    public Card getCard(int cardId) throws SQLException {
        Card card = null;

        String query = "SELECT * FROM card WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, cardId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int id = rs.getInt("id");
                    String number = rs.getString("number");
                    String color = rs.getString("color");
                    int playerId = rs.getInt("id_player");
                    card = new Card(id, number, color, playerId);
                }
            }
        }

        return card;
    }

	@Override
	public void saveGame(Card card) throws SQLException {

        final String URL = "jdbc:mysql://localhost:3306/uno";
        final String USER = "root";
        final String PASSWORD = "";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
            String sql = "INSERT INTO game (id_card) VALUES (?)";

            try (PreparedStatement statement = conn.prepareStatement(sql)) {
                statement.setInt(1, card.getId());

                statement.executeUpdate();
            }
        } catch (SQLException e) {
            System.err.println("Error al guardar el juego en la base de datos: " + e.getMessage());
            throw e;
        }


    }
	

	@Override
    public void saveCard(Card card) throws SQLException {
        String insertQuery = "INSERT INTO card (number, color, id_player) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, card.getNumber().toString());
            stmt.setString(2, card.getColor().toString());
            stmt.setInt(3, card.getPlayerId());
            stmt.executeUpdate();

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int cardId = generatedKeys.getInt(1);
                    card.setId(cardId);
                    System.out.println("¡Se ha guardado la carta en la base de datos con ID " + cardId + "!");
                } else {
                    throw new SQLException("No se pudo obtener el ID de la carta generada.");
                }
            }
        }
    }

	@Override
    public void deleteCard(Card card) throws SQLException {
        String deleteQuery = "DELETE FROM card WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(deleteQuery)) {
            stmt.setInt(1, card.getId());
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                System.out.println("¡Se ha eliminado la carta con ID " + card.getId() + " de la base de datos!");
            } else {
                System.out.println("No se encontró la carta con ID " + card.getId() + " en la base de datos.");
            }
        }
    }

	@Override
    public void clearDeck(int playerId) throws SQLException {
        String deleteQuery = "DELETE FROM card WHERE id_player = ?";
        try (PreparedStatement stmt = conn.prepareStatement(deleteQuery)) {
            stmt.setInt(1, playerId);
            stmt.executeUpdate();
            System.out.println("¡Se han eliminado todas las cartas del jugador con ID " + playerId + "!");
        }
    }

	@Override
    public void addVictories(int playerId) throws SQLException {
        String updateQuery = "UPDATE player SET victories = victories + 1 WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(updateQuery)) {
            stmt.setInt(1, playerId);
            stmt.executeUpdate();
            System.out.println("¡Victoria añadida para el jugador con ID " + playerId + "!");
        }
    }


	@Override
    public void addGames(int playerId) throws SQLException {
        String updateQuery = "UPDATE player SET games = games + 1 WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(updateQuery)) {
            stmt.setInt(1, playerId);
            stmt.executeUpdate();
            System.out.println("¡Juego añadido para el jugador con ID " + playerId + "!");
        }
    }

}