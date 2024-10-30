package dataaccess;

import dataaccess.exceptions.DataAccessException;
import model.GameData;

import java.util.Collection;

public class MySqlGameDataAccess {

    public Collection<GameData> getAllGames() throws DataAccessException{};

    public GameData createGame(String gameName) throws DataAccessException{};

    public GameData getGame(int gameID) throws DataAccessException{};

    public void updateGame(GameData gameData, String playerColor, String username) throws DataAccessException {};

    public void clearAllGames() throws DataAccessException {};
}
