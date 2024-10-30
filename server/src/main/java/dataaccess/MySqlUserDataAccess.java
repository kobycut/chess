package dataaccess;

import dataaccess.exceptions.DataAccessException;
import model.UserData;

public class MySqlUserDataAccess {

    public UserData getUser(String username) throws DataAccessException{};

    public void createUser(UserData userData) throws DataAccessException{};

    public void clearAllUsers() throws DataAccessException{};

}
