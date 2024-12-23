package service;


import dataaccess.AuthDAO;
import exceptions.DataAccessException;
import dataaccess.GameDAO;
import dataaccess.UserDAO;

public class ClearApplication {
    private final AuthDAO authDAO;
    private final GameDAO gameDAO;
    private final UserDAO userDAO;

    public ClearApplication(UserDAO userDAO, GameDAO gameDAO, AuthDAO authDAO) {
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
        this.userDAO = userDAO;
    }
    public void clearAll() throws DataAccessException {
        userDAO.clearAllUsers();
        gameDAO.clearAllGames();
        authDAO.clearAllAuthTokens();
    }
}