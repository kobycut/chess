package service;
import dataaccess.*;
import model.AuthData;

public class Logout {

    private final AuthDAO authDAO;

    public Logout(AuthDAO authDAO){
        this.authDAO = authDAO;
    }

    public AuthData getAuthData(String authToken) throws DataAccessException {
        return authDAO.getAuthData(authToken);
    }
    public void deleteAuthData(AuthData authData) throws DataAccessException {
        authDAO.deleteAuth(authData);
    }
}
