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

    public GameData createGame(GameData gameData, String authToken) throws DataAccessException {
        AuthData authData = authDAO.getAuthData(authToken);
        if (authData == null) {
            // throw error
        }
        // Another error somewhere, bad request

        return gameDAO.createGame(gameData.gameName());

    }


}
