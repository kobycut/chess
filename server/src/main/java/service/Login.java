package service;

import dataaccess.*;
import dataaccess.exceptions.DataAccessException;
import dataaccess.exceptions.UnauthorizedException;
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
//        if (!userData.password().equals(userDAO.getUser(userData.username()).password())) {
//            throw new UnauthorizedException(401);
//        }
//        String hashedPassword = BCrypt.hashpw(userDAO.getUser(userData.username()).password(), BCrypt.gensalt());

        String hashedPassword = userDAO.getUser(userData.username()).password();

        if (!BCrypt.checkpw(userData.password(), hashedPassword)) {
            throw new UnauthorizedException(401);
        }
        AuthData authData = new AuthData(UUID.randomUUID().toString(), userData.username());
        authDAO.createAuth(authData);
        return authData;
    }

}