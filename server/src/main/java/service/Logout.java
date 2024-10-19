package service;
import dataaccess.*;
import dataaccess.exceptions.DataAccessException;
import dataaccess.exceptions.UnauthorizedException;
import model.AuthData;

public class Logout {

    private final AuthDAO authDAO;

    public Logout(AuthDAO authDAO){
        this.authDAO = authDAO;
    }

    public void logout(String authToken) throws UnauthorizedException {
        AuthData authData = authDAO.getAuthData(authToken);
        if (authData == null) {
            throw new UnauthorizedException(401);
        }
        authDAO.deleteAuth(authData);
    }
}

// TODO throw 500 error