package dataaccess;

import dataaccess.exceptions.DataAccessException;
import model.AuthData;

public interface AuthDAO {

    AuthData createAuth(AuthData authData);

    AuthData getAuthData(String authToken);

    void deleteAuth(AuthData authData);

    void clearAllAuthTokens();

}
