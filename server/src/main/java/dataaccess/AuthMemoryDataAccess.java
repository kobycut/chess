package dataaccess;

import dataaccess.exceptions.DataAccessException;
import model.AuthData;

import java.util.HashMap;

public class AuthMemoryDataAccess implements AuthDAO {
    final private HashMap<String, AuthData> auths = new HashMap<>();

    @Override
    public AuthData createAuth(AuthData authData) throws DataAccessException{
        auths.put(authData.authToken(), authData);
        return authData;
    }

    @Override
    public AuthData getAuthData(String authToken) throws DataAccessException{
        return auths.get(authToken);
    }

    @Override
    public void deleteAuth(AuthData authData) throws DataAccessException{
        auths.remove(authData.authToken());
    }

    @Override
    public void clearAllAuthTokens() throws DataAccessException{
        auths.clear();
    }
}
