package dataaccess;
import dataaccess.MySqlAuthDataAccess.*;
import static org.junit.jupiter.api.Assertions.*;


import dataaccess.exceptions.AlreadyTakenException;
import dataaccess.exceptions.DataAccessException;
import model.AuthData;
import model.GameData;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

public class DataAccessTest {


    @Test
    public void goodCreateAuth() throws DataAccessException {
        var dataAccess = new MySqlAuthDataAccess();
        dataAccess.clearAllAuthTokens();
        AuthData authData = new AuthData("authTokenRighthere", "Stevey");


        AuthData testAuthData = dataAccess.createAuth(authData);

        assertEquals(authData, testAuthData);
    }
    @Test
    public void badCreateAuth() throws DataAccessException {
        var dataAccess = new MySqlAuthDataAccess();
        dataAccess.clearAllAuthTokens();
        AuthData authData = new AuthData(null, "Stevey");


        assertThrows(DataAccessException.class, () -> dataAccess.createAuth(authData));

    }
    @Test
    public void goodGetAuth() throws DataAccessException {
        var dataAccess = new MySqlAuthDataAccess();
        dataAccess.clearAllAuthTokens();

        AuthData authData = new AuthData("authTokenRighthere", "Stevey");

        dataAccess.createAuth(authData);

        AuthData testAuthData = dataAccess.getAuthData(authData.authToken());

        assertEquals(authData, testAuthData);


    }
    @Test
    public void badGetAuth() throws DataAccessException {
        var dataAccess = new MySqlAuthDataAccess();
        dataAccess.clearAllAuthTokens();

        AuthData authData = new AuthData("authTokenRightHere", "Stevey");

        dataAccess.createAuth(authData);

        var testAuthData = dataAccess.getAuthData("aFakeToken");

        assertNull(testAuthData);

    }
    @Test
    public void goodDeleteAuth() throws DataAccessException {
        var dataAccess = new MySqlAuthDataAccess();
        dataAccess.clearAllAuthTokens();

        AuthData authData = new AuthData("authTokenRightHere", "Stevey");
        dataAccess.createAuth(authData);

        dataAccess.deleteAuth(authData);

        var testAuthData = dataAccess.getAuthData(authData.authToken());

        assertNull(testAuthData);


    }
    @Test
    public void badDeleteAuth() throws DataAccessException {
        var dataAccess = new MySqlAuthDataAccess();
        dataAccess.clearAllAuthTokens();

        AuthData authData = new AuthData("authTokenRightHere", "Stevey");
        dataAccess.createAuth(authData);

        dataAccess.deleteAuth(new AuthData("afakeone", "FrankyMan"));

        var testAuthData = dataAccess.getAuthData(authData.authToken());

        assertEquals(authData, testAuthData);


    }
    @Test
    public void goodClearAuthtokens() throws DataAccessException {
        var dataAccess = new MySqlAuthDataAccess();
        dataAccess.clearAllAuthTokens();

        AuthData authData = new AuthData("authTokenRightHere", "Stevey");
        dataAccess.createAuth(authData);

        dataAccess.clearAllAuthTokens();

        var testAuthData = dataAccess.getAuthData(authData.authToken());

        assertNull(testAuthData);

    }
    @Test
    public void goodGetAllGames() throws DataAccessException {
        var dataAccess = new MySqlGameDataAccess();
        dataAccess.clearAllGames();

        dataAccess.createGame("Franky's first game");
        dataAccess.createGame("Franky's second game");
        dataAccess.createGame("Franky's third game");

        var games = dataAccess.getAllGames();

        var expected = new ArrayList<GameData>();
        assertEquals();

    }
    @Test
    public void badGetAllGames() throws DataAccessException {
        var dataAccess = new MySqlGameDataAccess();
        dataAccess.clearAllGames();

    }
    @Test
    public void goodCreateGame() throws DataAccessException {
        var dataAccess = new MySqlGameDataAccess();
        dataAccess.clearAllGames();

    }
    @Test
    public void badCreateGame() throws DataAccessException {
        var dataAccess = new MySqlGameDataAccess();
        dataAccess.clearAllGames();

    }
    @Test
    public void goodGetGame() throws DataAccessException {
        var dataAccess = new MySqlGameDataAccess();
        dataAccess.clearAllGames();

    }
    @Test
    public void badGetGame() throws DataAccessException {
        var dataAccess = new MySqlGameDataAccess();
        dataAccess.clearAllGames();

    }
    @Test
    public void goodUpdateGame() throws DataAccessException {
        var dataAccess = new MySqlGameDataAccess();
        dataAccess.clearAllGames();

    }
    @Test
    public void badUpdateGame() throws DataAccessException {
        var dataAccess = new MySqlGameDataAccess();
        dataAccess.clearAllGames();

    }
    @Test
    public void goodClearAllGames() throws DataAccessException {
        var dataAccess = new MySqlGameDataAccess();
        dataAccess.clearAllGames();

    }
    @Test
    public void goodGetUser() throws DataAccessException {
        var dataAccess = new MySqlUserDataAccess();
        dataAccess.clearAllUsers();

    }
    @Test
    public void badGetUser() throws DataAccessException {
        var dataAccess = new MySqlUserDataAccess();
        dataAccess.clearAllUsers();


    }
    @Test
    public void goodCreateUser() throws DataAccessException {
        var dataAccess = new MySqlUserDataAccess();
        dataAccess.clearAllUsers();

    }
    @Test
    public void badCreateUser() throws DataAccessException {
        var dataAccess = new MySqlUserDataAccess();
        dataAccess.clearAllUsers();

    }
    @Test
    public void goodClearAllUsers() throws DataAccessException {
        var dataAccess = new MySqlUserDataAccess();
        dataAccess.clearAllUsers();

    }

}
