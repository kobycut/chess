package service;

import dataaccess.*;
import model.*;

public class Register {

    private final UserDAO userDAO;
    private final AuthDAO authDAO;

    public Register(UserDAO userDAO, AuthDAO authDAO) {
        this.userDAO = userDAO;
        this.authDAO = authDAO;
    }

    public UserData getUser(String username) throws DataAccessException {
        return userDAO.getUser(username);
    }

    public void createUser(UserData userData) throws DataAccessException {
        userDAO.createUser(userData);
    }

    public AuthData createAuth(AuthData authData) throws DataAccessException {
        return authDAO.createAuthWithData(authData);
    }

}
