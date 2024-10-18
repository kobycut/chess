package dataaccess;

import model.AuthData;

public interface AuthDAO {

    AuthData createAuth(AuthData authData) throws DataAccessException;

    AuthData getAuthData(String authToken) throws DataAccessException;

    void deleteAuth(AuthData authData) throws DataAccessException;

    void clearAllAuthTokens() throws DataAccessException;

}
