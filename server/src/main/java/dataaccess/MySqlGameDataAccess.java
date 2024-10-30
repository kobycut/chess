package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import dataaccess.exceptions.DataAccessException;
import model.GameData;

import java.sql.SQLException;
import java.util.Collection;

public class MySqlGameDataAccess {
    public MySqlGameDataAccess() throws DataAccessException {
        configureDatabase();
    }

    public Collection<GameData> getAllGames() throws DataAccessException{

    };

    public GameData createGame(String gameName) throws DataAccessException{
        var statement = "INSERT INTO game (gameName, chessGame) VALUES (?, ?)";
        var statement2 = "SELECT * FROM game WHERE gameName=?";
        ChessGame newGame = new ChessGame();

        try (var preparedStatement = DatabaseManager.getConnection().prepareStatement(statement)) {
            preparedStatement.setString(1, gameName);
            var json = new Gson().toJson(newGame);
            preparedStatement.setString(2, json);
            preparedStatement.executeUpdate();

            try (var preparedStatement2 = DatabaseManager.getConnection().prepareStatement(statement2)) {
                preparedStatement2.setString(1, gameName);
                var rs = preparedStatement2.executeQuery();
                Integer gameId = rs.getInt("gameID");
                String whiteUsername = rs.getString("whiteUsername");
                String blackUsername = rs.getString("blackUsername");

                var json2 = rs.getString("chessGame");
                var chessGame = new Gson().fromJson(json2, ChessGame.class);

                return new GameData(gameId, whiteUsername, blackUsername, gameName, chessGame);
            } catch (SQLException e) {
                throw new DataAccessException(500, e.getMessage());
            }

        } catch (SQLException e) {
            throw new DataAccessException(500, e.getMessage());
        }
    };

    public GameData getGame(int gameID) throws DataAccessException{
        var statement = "SELECT * FROM game WHERE gameID=?";
        try (var preparedStatement = DatabaseManager.getConnection().prepareStatement(statement)) {
            preparedStatement.setInt(1, gameID);
            var rs = preparedStatement.executeQuery();

            String gameName = rs.getString("gameName");
            String whiteUsername = rs.getString("whiteUsername");
            String blackUsername = rs.getString("blackUsername");
            var json = rs.getString("chessGame");
            var chessGame = new Gson().fromJson(json, ChessGame.class);

            return new GameData(gameID, whiteUsername, blackUsername, gameName, chessGame);
        } catch (SQLException e) {
            throw new DataAccessException(500, e.getMessage());
        }

    };

    public void updateGame(GameData gameData, String playerColor, String username) throws DataAccessException {

    };

    public void clearAllGames() throws DataAccessException {
        var statement = "DELETE FROM game";
        try (var preparedStatement = DatabaseManager.getConnection().prepareStatement(statement)) {
            preparedStatement.executeQuery();
        } catch (SQLException e) {
            throw new DataAccessException(500, e.getMessage());
        }

    };

    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS game (
            'gameName' string,
            'gameID' int AUTO_INCREMENT,
            'whiteUsername' string,
            'blackUsername' string,
            'chessGame' ChessGame,
            PRIMARY KEY ('gameID')
            )
 
            """

    };

    private void configureDatabase() throws DataAccessException {
        DatabaseManager.createDatabase();
        try (var conn = DatabaseManager.getConnection()) {
            for (var statement : createStatements) {
                try (var preparedStatement = conn.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
        } catch (SQLException ex) {
            throw new DataAccessException(500, String.format("Unable to configure database: %s", ex.getMessage()));
        }
    }


}
