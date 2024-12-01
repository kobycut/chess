package service;

import exceptions.AlreadyTakenException;
import exceptions.BadRequestException;
import exceptions.UnauthorizedException;
import model.AuthData;
import model.GameData;
import dataaccess.AuthDAO;
import exceptions.DataAccessException;
import dataaccess.GameDAO;

import java.util.Objects;

public class JoinGame {

    private final AuthDAO authDAO;
    private final GameDAO gameDAO;

    public JoinGame(AuthDAO authDAO, GameDAO gameDAO) {
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
    }

    public GameData join(String authToken, GameData gameData, String playerColor)
            throws UnauthorizedException, DataAccessException, BadRequestException, AlreadyTakenException {

        AuthData authData = authDAO.getAuthData(authToken);
        if (playerColor == null) {
            throw new BadRequestException(400);
        }
        if (authData == null) {
            throw new UnauthorizedException(401);
        }
        GameData game = gameDAO.getGame(gameData.gameID());
        if (game == null) {
            throw new BadRequestException(400);
        }
        if (Objects.equals(playerColor, "WHITE") && game.whiteUsername() != null) {
            throw new AlreadyTakenException(403);
        }
        if (Objects.equals(playerColor, "BLACK") && game.blackUsername() != null) {
            throw new AlreadyTakenException(403);
        }

        gameDAO.updateGame(game, playerColor, authData.username());
        return gameDAO.getGame(game.gameID());
    }
}
