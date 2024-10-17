package service;


import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import dataaccess.UserDAO;

public class ClearApplication {
    private final AuthDAO authDAO;
    private final GameDAO gameDAO;
    private final UserDAO userDAO;

    public ClearApplication(UserDAO userDAO, GameDAO gameDAO, AuthDAO authDAO, AuthDAO authDAO1, GameDAO gameDAO1, UserDAO userDAO1) {
        this.authDAO = authDAO1;
        this.gameDAO = gameDAO1;
        this.userDAO = userDAO1;
    }

    public void clearAllUsers() {}
    public void clearAllGames() {}
    public void clearAllAuthTokens() {}
}
