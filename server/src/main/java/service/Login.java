package service;
import dataaccess.*;
import model.AuthData;
import model.UserData;

public class Login {

    private final UserDAO userDAO;
    private final AuthDAO authDAO;

    public Login(UserDAO userDAO, AuthDAO authDAO)  {
        this.userDAO = userDAO;
        this.authDAO = authDAO;
    }

    public UserData getUserData(String username) throws DataAccessException {
        return userDAO.getUser(username);
    }

    public AuthData createAuth(String username) throws DataAccessException {
        return authDAO.createAuthWithString(username);
    }


}
