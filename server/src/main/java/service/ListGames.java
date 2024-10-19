package service;
import dataaccess.exceptions.DataAccessException;
import dataaccess.exceptions.UnauthorizedException;
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

    public Collection<GameData> listGames(String authToken) throws UnauthorizedException {
        AuthData authData = authDAO.getAuthData(authToken);
        if (authData == null) {
            throw new UnauthorizedException(401);
        }
        return gameDAO.getAllGames();
    }

}

// TODO throw 500 error
