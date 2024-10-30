package dataaccess;
import dataaccess.MySqlAuthDataAccess.*;
import static org.junit.jupiter.api.Assertions.*;


import dataaccess.exceptions.AlreadyTakenException;
import dataaccess.exceptions.DataAccessException;
import model.AuthData;
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
    public void badGetAuth() {

    }
    @Test
    public void goodDeleteAuth() {

    }
    @Test
    public void badDeleteAuth() {

    }
    @Test
    public void goodClearAuthtokens() {

    }
    @Test
    public void goodGetAllGames() {

    }
    @Test
    public void badGetAllGames() {

    }
    @Test
    public void goodCreateGame() {

    }
    @Test
    public void badCreateGame() {

    }
    @Test
    public void goodGetGame() {

    }
    @Test
    public void badGetGame() {

    }
    @Test
    public void goodUpdateGame() {

    }
    @Test
    public void badUpdateGame() {

    }
    @Test
    public void goodClearAllGames() {

    }
    @Test
    public void goodGetUser() {

    }
    @Test
    public void badGetUser() {

    }
    @Test
    public void goodCreateUser() {

    }
    @Test
    public void badCreateUser() {

    }
    @Test
    public void goodClearAllUsers() {

    }

}
