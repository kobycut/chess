package service;

import dataaccess.*;
import model.*;

import java.util.UUID;

public class Register {

    private final UserDAO userDAO;
    private final AuthDAO authDAO;

    public Register(UserDAO userDAO, AuthDAO authDAO) {
        this.userDAO = userDAO;
        this.authDAO = authDAO;
    }

    public AuthData register(UserData userData) throws DataAccessException {
        if (userDAO.getUser(userData.username()) != null) {
            // throw error
        }
        userDAO.createUser(userData);
        AuthData authData = new AuthData(UUID.randomUUID().toString(), userData.username());
        authDAO.createAuth(authData);
        return authData;
    }

}
