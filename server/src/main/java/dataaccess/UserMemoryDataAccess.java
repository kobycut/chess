package dataaccess;

import dataaccess.exceptions.DataAccessException;
import model.UserData;

import java.util.HashMap;

public class UserMemoryDataAccess implements UserDAO  {
    final private HashMap<String, UserData> users = new HashMap<>();

    @Override
    public UserData getUser(String username) throws DataAccessException{
        return users.get(username);
    }

    @Override
    public void createUser(UserData userData) throws DataAccessException{
        users.put(userData.username(), userData);
    }

    @Override
    public void clearAllUsers() throws DataAccessException{
        users.clear();
    }
}
