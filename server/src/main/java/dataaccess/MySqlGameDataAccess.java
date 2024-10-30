package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import dataaccess.exceptions.DataAccessException;
import model.GameData;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

public class MySqlGameDataAccess implements GameDAO{
    public MySqlGameDataAccess() throws DataAccessException {
        configureDatabase();
    }

    public Collection<GameData> getAllGames() throws DataAccessException {
        var games = new ArrayList<GameData>();
        var statement = "SELECT gameName, gameID, whiteUsername, blackUsername, chessGame FROM game";
        try (var preparedStatement = DatabaseManager.getConnection().prepareStatement(statement)) {
            try (var rs = preparedStatement.executeQuery()) {
                while (rs.next()) {
                    var gameName = rs.getString("gameName");
                    var gameId = rs.getInt("gameID");
                    var whiteUsername = rs.getString("whiteUsername");
                    var blackUsername = rs.getString("blackUsername");
                    var json = rs.getString("chessGame");
                    var chessGame = new Gson().fromJson(json, ChessGame.class);

                    games.add(new GameData(gameId, whiteUsername, blackUsername, gameName, chessGame));
                }
            }
            return games;
        } catch (SQLException e) {
            throw new DataAccessException(500, e.getMessage());
        }
    }

    ;

    public GameData createGame(String gameName) throws DataAccessException {
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
                if (rs.next()) {
                    Integer gameId = rs.getInt("gameID");
                    String whiteUsername = rs.getString("whiteUsername");
                    String blackUsername = rs.getString("blackUsername");
                    var json2 = rs.getString("chessGame");
                    var chessGame = new Gson().fromJson(json2, ChessGame.class);
                    return new GameData(gameId, whiteUsername, blackUsername, gameName, chessGame);
                } else {
                    throw new DataAccessException(500, "No game was found");
                }
            }

        } catch (SQLException e) {
            throw new DataAccessException(500, e.getMessage());
        }
    }

    ;

    public GameData getGame(int gameID) throws DataAccessException {
        var statement = "SELECT * FROM game WHERE gameID=?";
        try (var preparedStatement = DatabaseManager.getConnection().prepareStatement(statement)) {
            preparedStatement.setInt(1, gameID);
            var rs = preparedStatement.executeQuery();
            if (rs.next()) {
                String gameName = rs.getString("gameName");
                String whiteUsername = rs.getString("whiteUsername");
                String blackUsername = rs.getString("blackUsername");
                var json = rs.getString("chessGame");
                var chessGame = new Gson().fromJson(json, ChessGame.class);
                return new GameData(gameID, whiteUsername, blackUsername, gameName, chessGame);
            } else {
                throw new DataAccessException(500, "No game found");
            }
        } catch (SQLException e) {
            throw new DataAccessException(500, e.getMessage());
        }

    }

    ;

    public void updateGame(GameData gameData, String playerColor, String username) throws DataAccessException {
        GameData updatedGameData = null;
        if (Objects.equals(playerColor, "WHITE")) {
            updatedGameData = new GameData(gameData.gameID(), username, gameData.blackUsername(), gameData.gameName(), gameData.chessGame());
        }
        if (Objects.equals(playerColor, "BLACK")) {
            updatedGameData = new GameData(gameData.gameID(), gameData.whiteUsername(), username, gameData.gameName(), gameData.chessGame());
        }
        if (updatedGameData == null) {
            throw new DataAccessException(500, "No color given");
        }
        var statement = "UPDATE game SET gameName=?, whiteUsername=?, blackUsername=?, chessGame=? WHERE gameID=?";
        try (var preparedStatement = DatabaseManager.getConnection().prepareStatement(statement)) {
            preparedStatement.setString(1, updatedGameData.gameName());
            preparedStatement.setString(2, updatedGameData.whiteUsername());
            preparedStatement.setString(3, updatedGameData.blackUsername());
            var json = new Gson().toJson(updatedGameData.chessGame());
            preparedStatement.setString(4, json);
            preparedStatement.setInt(5, updatedGameData.gameID());
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            throw new DataAccessException(500, e.getMessage());
        }

    }

    ;

    public void clearAllGames() throws DataAccessException {
        var statement = "DELETE FROM game";
        try (var preparedStatement = DatabaseManager.getConnection().prepareStatement(statement)) {
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException(500, e.getMessage());
        }

    }

    ;

    private final String[] createStatements = {"""
            CREATE TABLE IF NOT EXISTS game (
            gameName VARCHAR(255),
            gameID INT AUTO_INCREMENT,
            whiteUsername VARCHAR(255) DEFAULT NULL,
            blackUsername VARCHAR(255) DEFAULT NULL,
            chessGame TEXT,
            PRIMARY KEY (gameID)
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
