package dataaccess;

import model.AuthData;

public interface AuthDAO {

    AuthData createAuthWithData(AuthData authData) throws DataAccessException;

    AuthData createAuthWithString(String username) throws DataAccessException;

    AuthData getAuthData(String authToken) throws DataAccessException;

    void deleteAuth(AuthData authData) throws DataAccessException;

    void clearAllAuthTokens() throws DataAccessException;

}
