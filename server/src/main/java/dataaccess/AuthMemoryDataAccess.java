package dataaccess;

import model.AuthData;

public class AuthMemoryDataAccess implements AuthDAO {

    @Override
    public AuthData createAuthWithData(AuthData authData) throws DataAccessException {
        return null;
    }

    @Override
    public AuthData createAuthWithString(String username) throws DataAccessException {
        return null;
    }

    @Override
    public AuthData getAuthData(String authToken) throws DataAccessException {
        return null;
    }

    @Override
    public void deleteAuth(AuthData authData) throws DataAccessException {

    }

    @Override
    public void clearAllAuthTokens() throws DataAccessException {

    }
}
