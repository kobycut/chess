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
        UserData userData = userDAO.getUser(username);
        if (userData == null) {
            // throw error
        }
        userDAO.createUser(userData);
        return userData;
    }

    public AuthData createAuth(AuthData authData) throws DataAccessException {
        return authDAO.createAuthWithData(authData);
    }

}
