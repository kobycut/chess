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

    public void join(String authToken, GameData gameData) throws DataAccessException {
        AuthData authData = authDAO.getAuthData(authToken);
        if (authData == null) {
            // throw error
        }
        GameData game = gameDAO.getGame(gameData.gameID());
        if (game == null) {
            // throw error
        }
        gameDAO.updateGame(game);
    }
}
