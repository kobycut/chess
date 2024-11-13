package service;
import dataaccess.*;
import exceptions.DataAccessException;
import exceptions.UnauthorizedException;
import model.AuthData;

public class Logout {

    private final AuthDAO authDAO;

    public Logout(AuthDAO authDAO){
        this.authDAO = authDAO;
    }

    public void logout(String authToken) throws UnauthorizedException, DataAccessException {
        AuthData authData = authDAO.getAuthData(authToken);
        if (authData == null) {
            throw new UnauthorizedException(401);
        }
        authDAO.deleteAuth(authData);
    }
}
