package service;

import dataaccess.AuthDAO;
import dataaccess.exceptions.BadRequestException;
import dataaccess.exceptions.DataAccessException;
import dataaccess.GameDAO;
import dataaccess.exceptions.UnauthorizedException;
import model.AuthData;
import model.GameData;

public class CreateGame {

    private final AuthDAO authDAO;
    private final GameDAO gameDAO;

    public CreateGame(AuthDAO authDAO, GameDAO gameDAO) {
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
    }

    public GameData createGame(GameData gameData, String authToken) throws UnauthorizedException, DataAccessException, BadRequestException {
        AuthData authData = authDAO.getAuthData(authToken);
        if (authData == null) {
            throw new UnauthorizedException(401);
        }
        if (gameData.gameName() == null) {
            throw new BadRequestException(400);
        }


        return gameDAO.createGame(gameData.gameName());

    }


}

