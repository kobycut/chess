package dataaccess;

import model.AuthData;

import java.util.HashMap;

public class AuthMemoryDataAccess implements AuthDAO {
    final private HashMap<String, AuthData> auths = new HashMap<>();

    @Override
    public AuthData createAuthWithData(AuthData authData) throws DataAccessException {
        auths.put(authData.authToken(), authData);
        return
    }

    @Override
    public AuthData createAuthWithString(String username) throws DataAccessException {
        return null;
    }

    @Override
    public AuthData getAuthData(String authToken) throws DataAccessException {
        return auths.get(authToken);
    }

    @Override
    public void deleteAuth(AuthData authData) throws DataAccessException {
        auths.remove(authData.authToken());
    }

    @Override
    public void clearAllAuthTokens() throws DataAccessException {
        auths.clear();
    }
}
