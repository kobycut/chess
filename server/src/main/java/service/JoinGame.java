package service;

import model.AuthData;
import model.GameData;
import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;

public class JoinGame {

    private final AuthDAO authDAO;
    private final GameDAO gameDAO;

    public JoinGame(AuthDAO authDAO, GameDAO gameDAO) {
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
    }

    public AuthData getAuthData(String authToken) throws DataAccessException {
        return authDAO.getAuthData(authToken);
    }
    public GameData getGameData(int gameID) throws DataAccessException {
        return gameDAO.getGame(gameID);
    }
    public void updateGame(GameData gameData) throws DataAccessException {
        gameDAO.updateGame(gameData);
    }
}
