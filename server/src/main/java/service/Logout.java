package service;
import dataaccess.*;
import model.AuthData;

public class Logout {

    private final AuthDAO authDAO;

    public Logout(AuthDAO authDAO){
        this.authDAO = authDAO;
    }

    public void logout(String authToken) throws DataAccessException {
        AuthData authData = authDAO.getAuthData(authToken);
        if (authData == null) {
            // throw error
        }
        authDAO.deleteAuth(authData);
    }
}
