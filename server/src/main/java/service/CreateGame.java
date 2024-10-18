package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import model.AuthData;
import model.GameData;

public class CreateGame {

    private final AuthDAO authDAO;
    private final GameDAO gameDAO;

    public CreateGame(AuthDAO authDAO, GameDAO gameDAO) {
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
    }

    public AuthData getAuthData(String authToken)  {
        return authDAO.getAuthData(authToken);

    }
    public GameData createGame(String gameName) throws DataAccessException {
        return gameDAO.createGame(gameName);
    }


}
