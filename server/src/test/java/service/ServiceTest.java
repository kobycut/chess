package service;

import dataaccess.*;
import dataaccess.exceptions.AlreadyTakenException;
import dataaccess.exceptions.BadRequestException;
import dataaccess.exceptions.DataAccessException;
import model.AuthData;
import model.UserData;
import org.eclipse.jetty.server.Authentication;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import service.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

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

        UserData userData = new UserData("BrandNewUser", "CoolPassword", "BrandNew@cool.com");
        Register register = new Register(userDAO, authDAO);
        AuthData authData = register.register(userData);

        UserData duplicateUserData = new UserData("BrandNewUser", "DifferentCoolPassword", "Email@email.com");
        register.register(duplicateUserData);

        assertEquals("BrandNewUser", authData.username());

        assertEquals(userData ,userDAO.getUser("BrandNewUser"));

    }

    @Test
    public void GoodClearAll() {}

    @Test
    public void GoodLogin() {}

    @Test
    public void BadLogin() {}

    @Test
    public void GoodLogout() {}

    @Test
    public void BadLogout() {}

    @Test
    public void GoodCreateGame() {}

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
