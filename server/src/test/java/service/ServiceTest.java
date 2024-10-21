package service;

import chess.ChessGame;
import dataaccess.*;
import dataaccess.exceptions.AlreadyTakenException;
import dataaccess.exceptions.BadRequestException;
import dataaccess.exceptions.DataAccessException;
import dataaccess.exceptions.UnauthorizedException;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.eclipse.jetty.server.Authentication;
import org.eclipse.jetty.util.log.Log;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import service.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ServiceTest {
    static private UserDAO userDAO;
    static private AuthDAO authDAO;
    static private GameDAO gameDAO;

    @BeforeAll
    public static void init() {
        userDAO = new UserMemoryDataAccess();
        authDAO = new AuthMemoryDataAccess();
        gameDAO = new GameMemoryDataAccess();

    }
    @Test
    public void GoodRegisterUser() throws BadRequestException, AlreadyTakenException, DataAccessException {
        ClearApplication clear = new ClearApplication(userDAO, gameDAO, authDAO);
        clear.clearAll();

        UserData userData = new UserData("BrandNewUser", "CoolPassword", "BrandNew@cool.com");
        Register register = new Register(userDAO, authDAO);
        AuthData authData = register.register(userData);

        assertEquals("BrandNewUser", authData.username());

        assertEquals(userData ,userDAO.getUser("BrandNewUser"));


    }
    @Test
    public void BadRegisterUser() throws BadRequestException, AlreadyTakenException, DataAccessException {
        ClearApplication clear = new ClearApplication(userDAO, gameDAO, authDAO);
        clear.clearAll();

        Register register = new Register(userDAO, authDAO);

        register.register(new UserData("BrandNewUser", "CoolPassword", "SickEmail@email.com"));

        UserData duplicateUserData = new UserData("BrandNewUser", "DifferentCoolPassword", "Email@email.com");

        assertThrows(AlreadyTakenException.class, () -> register.register(duplicateUserData));

    }

    @Test
    public void GoodClearAll() throws DataAccessException, BadRequestException, AlreadyTakenException {
        Register register = new Register(userDAO, authDAO);
        register.register(new UserData("BrandNewUser", "CoolPassword", "SickEmail@email.com"));
        register.register(new UserData("AnotherUser", "SweetPassword", "Email@Email.com"));

        ClearApplication clear = new ClearApplication(userDAO, gameDAO, authDAO);
        clear.clearAll();

        assertEquals(null, userDAO.getUser("BrandNewUser"));
    }

    @Test
    public void GoodLogin() throws UnauthorizedException, DataAccessException, BadRequestException, AlreadyTakenException {
        ClearApplication clear = new ClearApplication(userDAO, gameDAO, authDAO);
        clear.clearAll();

        Login login = new Login(userDAO, authDAO);
        Register register = new Register(userDAO, authDAO);

        UserData userData = new UserData("Scott", "ScottIsRad","Scott@Scott.mail");

        register.register(userData);
        AuthData authData = login.login(userData);

        assertEquals("Scott", authData.username());
    }

    @Test
    public void BadLogin() throws DataAccessException {
        ClearApplication clear = new ClearApplication(userDAO, gameDAO, authDAO);
        clear.clearAll();

        Login login = new Login(userDAO, authDAO);

        assertThrows(BadRequestException.class, () -> login.login(new UserData("Scott", "ScottTheBoss", "Scott@Scotty.com")));
    }

    @Test
    public void GoodLogout() {
        ClearApplication clear = new ClearApplication(userDAO, gameDAO, authDAO);
        clear.clearAll();
    }

    @Test
    public void BadLogout() {
        ClearApplication clear = new ClearApplication(userDAO, gameDAO, authDAO);
        clear.clearAll();
    }

    @Test
    public void GoodCreateGame() {
        ClearApplication clear = new ClearApplication(userDAO, gameDAO, authDAO);
        clear.clearAll();
    }

    @Test
    public void BadCreateGame() {}

    @Test
    public void GoodListGames() {}

    @Test
    public void BadListGames() {}

    @Test
    public void GoodJoin() {}

    @Test
    public void BadJoin() {}








}
