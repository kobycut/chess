package dataaccess;

import dataaccess.exceptions.DataAccessException;
import model.UserData;

public interface UserDAO {

    UserData getUser(String username);

    void createUser(UserData userData);

    void clearAllUsers();

}
