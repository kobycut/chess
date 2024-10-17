package service;
import dataaccess.DataAccessException;
import model.AuthData;
import dataaccess.*;
import model.GameData;

import java.util.Collection;


public class ListGames {

    private final AuthDAO authDAO;
    private final GameDAO gameDAO;

    public ListGames(AuthDAO authDAO, GameDAO gameDAO) throws DataAccessException {
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
    }

    public AuthData getAuthData(String authToken) throws DataAccessException {
        return authDAO.getAuthData(authToken);
    }

    public Collection<GameData> getAllGameData() throws DataAccessException {
        return gameDAO.getAllGames();
    }
}
