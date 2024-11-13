package dataaccess;

import chess.ChessGame;

import static org.junit.jupiter.api.Assertions.*;


import exceptions.DataAccessException;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.Test;

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

        assertEquals(3, games.size());

    }

    @Test
    public void badGetAllGames() throws DataAccessException {
        var dataAccess = new MySqlGameDataAccess();
        dataAccess.clearAllGames();

        var games = dataAccess.getAllGames();

        assertEquals(0, games.size());
    }

    @Test
    public void goodCreateGame() throws DataAccessException {
        var dataAccess = new MySqlGameDataAccess();
        dataAccess.clearAllGames();

        dataAccess.createGame("Timothy's game number 1");

        var game = dataAccess.getGame(1);

        assertEquals("Timothy's game number 1", game.gameName());
    }

    @Test
    public void badCreateGame() throws DataAccessException {
        var dataAccess = new MySqlGameDataAccess();
        dataAccess.clearAllGames();

        var test = dataAccess.createGame(null);
        assertNull(test);
    }

    @Test
    public void goodGetGame() throws DataAccessException {
        var dataAccess = new MySqlGameDataAccess();
        dataAccess.clearAllGames();

        dataAccess.createGame("superman");
        var test = dataAccess.getGame(1);

        assertEquals("superman", test.gameName());

    }

    @Test
    public void badGetGame() throws DataAccessException {
        var dataAccess = new MySqlGameDataAccess();
        dataAccess.clearAllGames();

        var test = dataAccess.getGame(243);
        assertNull(test);

    }

    @Test
    public void goodUpdateGame() throws DataAccessException {
        var dataAccess = new MySqlGameDataAccess();
        dataAccess.clearAllGames();

        GameData gameData = new GameData(1, null, "superman", "batman v superman", new ChessGame());
        dataAccess.createGame("batman v superman");
        dataAccess.updateGame(gameData, "WHITE", "batman");

        assertEquals("batman", dataAccess.getGame(1).whiteUsername());

    }

    @Test
    public void badUpdateGame() throws DataAccessException {
        var dataAccess = new MySqlGameDataAccess();
        dataAccess.clearAllGames();

        GameData gameData = new GameData(1, "batman", "superman", "batman v superman", new ChessGame());
        dataAccess.createGame("batman v superman");
        dataAccess.updateGame(gameData, "WHITE", null);

        assertNull(dataAccess.getGame(1).whiteUsername());

    }

    @Test
    public void goodClearAllGames() throws DataAccessException {
        var dataAccess = new MySqlGameDataAccess();
        dataAccess.clearAllGames();

        dataAccess.createGame("2");
        dataAccess.createGame("43jskd");

        dataAccess.clearAllGames();

        assertEquals(0, dataAccess.getAllGames().size());

    }

    @Test
    public void goodGetUser() throws DataAccessException {
        var dataAccess = new MySqlUserDataAccess();
        dataAccess.clearAllUsers();

        dataAccess.createUser(new UserData("Franky", "Franky's password", "email"));
        var test = dataAccess.getUser("Franky");

        assertEquals("email", test.email());

    }

    @Test
    public void badGetUser() throws DataAccessException {
        var dataAccess = new MySqlUserDataAccess();
        dataAccess.clearAllUsers();

        dataAccess.createUser(new UserData("Franky", "Franky's password", "email"));
        var test = dataAccess.getUser("Froogy");
        assertNull(test);


    }

    @Test
    public void goodCreateUser() throws DataAccessException {
        var dataAccess = new MySqlUserDataAccess();
        dataAccess.clearAllUsers();

        dataAccess.createUser(new UserData("Franky", "Franky's password", "email"));

        assertEquals("email", dataAccess.getUser("Franky").email());
    }

    @Test
    public void badCreateUser() throws DataAccessException {
        var dataAccess = new MySqlUserDataAccess();
        dataAccess.clearAllUsers();

        assertThrows(DataAccessException.class, () -> dataAccess.createUser(new UserData(null, null, null)));
    }

    @Test
    public void goodClearAllUsers() throws DataAccessException {
        var dataAccess = new MySqlUserDataAccess();
        dataAccess.clearAllUsers();

        dataAccess.createUser(new UserData("Franky", "Franky's password", "email"));
        dataAccess.clearAllUsers();

        assertNull(dataAccess.getUser("Franky"));

    }

}
