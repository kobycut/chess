package dataaccess;

import exceptions.DataAccessException;
import model.GameData;

import java.util.Collection;

public interface GameDAO {

    Collection<GameData> getAllGames() throws DataAccessException;

    GameData createGame(String gameName) throws DataAccessException;

    GameData getGame(int gameID) throws DataAccessException;

    void updateGame(GameData gameData, String playerColor, String username) throws DataAccessException;

    void clearAllGames() throws DataAccessException;

}
