package service;
import dataaccess.DataAccessException;
import model.AuthData;
import dataaccess.*;
import model.GameData;

import java.util.Collection;


public class ListGames {

    private final AuthDAO authDAO;
    private final GameDAO gameDAO;

    public ListGames(AuthDAO authDAO, GameDAO gameDAO) {
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
    }

    public Collection<GameData> listGames(String authToken) throws DataAccessException {
        AuthData authData = authDAO.getAuthData(authToken);
        if (authData == null) {
            // throw error
        }
        return gameDAO.getAllGames();
    }

}
