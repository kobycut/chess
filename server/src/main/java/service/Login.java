package service;

import dataaccess.*;
import model.AuthData;
import model.UserData;

public class Login {

    private final UserDAO userDAO;
    private final AuthDAO authDAO;

    public Login(UserDAO userDAO, AuthDAO authDAO) {
        this.userDAO = userDAO;
        this.authDAO = authDAO;
    }

    public AuthData login(UserData userData) throws DataAccessException {
        if (userDAO.getUser(userData.username()) == null) {
            // throw error
        }
        if (!userData.password().equals(userDAO.getUser(userData.username()).password())) {
            // throw error
        }

        return authDAO.createAuth();


    }

}
