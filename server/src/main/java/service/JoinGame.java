package service;

import dataaccess.exceptions.UnauthorizedException;
import model.AuthData;
import model.GameData;
import dataaccess.AuthDAO;
import dataaccess.exceptions.DataAccessException;
import dataaccess.GameDAO;

public class JoinGame {

    private final AuthDAO authDAO;
    private final GameDAO gameDAO;

    public JoinGame(AuthDAO authDAO, GameDAO gameDAO) {
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
    }

    public void join(String authToken, GameData gameData) throws UnauthorizedException {
        AuthData authData = authDAO.getAuthData(authToken);
        if (authData == null) {
            throw new UnauthorizedException(401);
        }
        GameData game = gameDAO.getGame(gameData.gameID());
        if (game == null) {
            // throw error
        }
        gameDAO.updateGame(game);
    }
}
// TODO throw 500 error, 403 error, and check for white or black player