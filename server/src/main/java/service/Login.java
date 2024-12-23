package service;

import dataaccess.*;
import exceptions.DataAccessException;
import exceptions.UnauthorizedException;
import model.AuthData;
import model.UserData;
import org.mindrot.jbcrypt.BCrypt;

import java.util.UUID;

public class Login {

    private final UserDAO userDAO;
    private final AuthDAO authDAO;

    public Login(UserDAO userDAO, AuthDAO authDAO) {
        this.userDAO = userDAO;
        this.authDAO = authDAO;
    }

    public AuthData login(UserData userData) throws UnauthorizedException, DataAccessException {

        if (userDAO.getUser(userData.username()) == null) {
            throw new UnauthorizedException(401);
        }
        UserData user = userDAO.getUser(userData.username());
        String hashedPassword = user.password();

        if (!BCrypt.checkpw(userData.password(), hashedPassword)) {
            throw new UnauthorizedException(401);
        }
        AuthData authData = new AuthData(UUID.randomUUID().toString(), userData.username());
        authDAO.createAuth(authData);
        return authData;
    }

}