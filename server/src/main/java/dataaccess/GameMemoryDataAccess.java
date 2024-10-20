package dataaccess;

import chess.ChessGame;
import dataaccess.exceptions.DataAccessException;
import model.GameData;

import java.util.Collection;
import java.util.HashMap;
import java.util.Objects;

public class GameMemoryDataAccess implements GameDAO {
    private int nextId = 1;
    final private HashMap<Integer, GameData> games = new HashMap<>();

    @Override
    public Collection<GameData> getAllGames() throws DataAccessException{
        return games.values();
    }

    @Override
    public GameData createGame(String gameName) throws DataAccessException {
        ChessGame newGame = new ChessGame();
        GameData game = new GameData(nextId, null, null, gameName, newGame);
        games.put(nextId, game);
        nextId++;
        return game;
    }

    @Override
    public GameData getGame(int gameID)throws DataAccessException {
        return games.get(gameID);
    }

    @Override
    public void updateGame(GameData gameData, String playerColor, String username) throws DataAccessException  {
        GameData updatedGameData = null;
        if (Objects.equals(playerColor, "WHITE")) {
            updatedGameData = new GameData(gameData.gameID(), username, gameData.blackUsername(), gameData.gameName(), gameData.ChessGame());
        }
        if (Objects.equals(playerColor, "BLACK")) {
            updatedGameData = new GameData(gameData.gameID(), gameData.whiteUsername(), username, gameData.gameName(), gameData.ChessGame());
        }
        games.put(gameData.gameID(), updatedGameData);
    }

    @Override
    public void clearAllGames() throws DataAccessException {
        games.clear();
    }
}
