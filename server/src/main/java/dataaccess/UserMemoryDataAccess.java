package dataaccess;

import dataaccess.exceptions.DataAccessException;
import model.UserData;

import java.util.HashMap;

public class UserMemoryDataAccess implements UserDAO {
    final private HashMap<String, UserData> users = new HashMap<>();

    @Override
    public UserData getUser(String username) {
        return users.get(username);
    }

    @Override
    public void createUser(UserData userData) {
        users.put(userData.username(), userData);
    }

    @Override
    public void clearAllUsers() {
        users.clear();
    }
}
