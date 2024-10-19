package service;

import dataaccess.*;
import dataaccess.exceptions.AlreadyTakenException;
import dataaccess.exceptions.DataAccessException;
import model.*;

import java.util.UUID;

public class Register {

    private final UserDAO userDAO;
    private final AuthDAO authDAO;

    public Register(UserDAO userDAO, AuthDAO authDAO) {
        this.userDAO = userDAO;
        this.authDAO = authDAO;
    }

    public AuthData register(UserData userData) throws AlreadyTakenException {
        if (userDAO.getUser(userData.username()) != null) {
            throw new AlreadyTakenException(403, "Error: already taken");
        }
        userDAO.createUser(userData);
        AuthData authData = new AuthData(UUID.randomUUID().toString(), userData.username());
        authDAO.createAuth(authData);
        return authData;
    }

}

// throw bad request
// throw 500 error
