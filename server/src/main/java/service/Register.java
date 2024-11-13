package service;

import dataaccess.*;
import exceptions.AlreadyTakenException;
import exceptions.BadRequestException;
import exceptions.DataAccessException;
import model.*;
import org.mindrot.jbcrypt.BCrypt;

import java.util.UUID;

public class Register {

    private final UserDAO userDAO;
    private final AuthDAO authDAO;

    public Register(UserDAO userDAO, AuthDAO authDAO) {
        this.userDAO = userDAO;
        this.authDAO = authDAO;
    }

    public AuthData register(UserData userData) throws AlreadyTakenException, DataAccessException, BadRequestException {
        if (userDAO.getUser(userData.username()) != null) {
            throw new AlreadyTakenException(403);
        }
        if (userData.password() == null || userData.username() == null || userData.email()==null) {
            throw new BadRequestException(400);
        }
        String hashedPassword = BCrypt.hashpw(userData.password(), BCrypt.gensalt());
        userDAO.createUser(new UserData(userData.username(), hashedPassword, userData.email()));

        AuthData authData = new AuthData(UUID.randomUUID().toString(), userData.username());
        authDAO.createAuth(authData);
        return authData;
    }

}

