package service;


import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
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

    public void clearUsers() throws DataAccessException {
        userDAO.clearAllUsers();

    }
    public void clearGames() throws DataAccessException {
        gameDAO.clearAllGames();
    }
    public void clearAuth() throws DataAccessException {
        authDAO.clearAllAuthTokens();
    }

}
