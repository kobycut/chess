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
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Collection;


import static org.junit.jupiter.api.Assertions.*;

public class ServiceTest {
    static private UserDAO userDAO;
    static private AuthDAO authDAO;
    static private GameDAO gameDAO;

    @BeforeAll
    public static void init() {
        userDAO = new MySqlUserDataAccess();
        authDAO = new MySqlAuthDataAccess();
        gameDAO = new MySqlGameDataAccess();

    }

    @Test
    public void goodRegisterUser() throws BadRequestException, AlreadyTakenException, DataAccessException {
        ClearApplication clear = new ClearApplication(userDAO, gameDAO, authDAO);
        clear.clearAll();

        UserData userData = new UserData("BrandNewUser", "CoolPassword", "BrandNew@cool.com");
        Register register = new Register(userDAO, authDAO);
        AuthData authData = register.register(userData);

        assertEquals("BrandNewUser", authData.username());


    }

    @Test
    public void badRegisterUser() throws BadRequestException, AlreadyTakenException, DataAccessException {
        ClearApplication clear = new ClearApplication(userDAO, gameDAO, authDAO);
        clear.clearAll();

        Register register = new Register(userDAO, authDAO);

        register.register(new UserData("BrandNewUser", "CoolPassword", "SickEmail@email.com"));

        UserData duplicateUserData = new UserData("BrandNewUser", "DifferentCoolPassword", "Email@email.com");

        assertThrows(AlreadyTakenException.class, () -> register.register(duplicateUserData));

    }

    @Test
    public void goodClearAll() throws DataAccessException, BadRequestException, AlreadyTakenException {
        Register register = new Register(userDAO, authDAO);
        register.register(new UserData("BrandNewUser", "CoolPassword", "SickEmail@email.com"));
        register.register(new UserData("AnotherUser", "SweetPassword", "Email@Email.com"));

        ClearApplication clear = new ClearApplication(userDAO, gameDAO, authDAO);
        clear.clearAll();

        assertNull(userDAO.getUser("BrandNewUser"));
    }

    @Test
    public void goodLogin() throws UnauthorizedException, DataAccessException, BadRequestException, AlreadyTakenException {
        ClearApplication clear = new ClearApplication(userDAO, gameDAO, authDAO);
        clear.clearAll();

        Login login = new Login(userDAO, authDAO);
        Register register = new Register(userDAO, authDAO);

//        var password = BCrypt.hashpw("ScottIsRad", BCrypt.gensalt());

        UserData userData = new UserData("Scott", "ScottIsRad", "Scott@Scott.mail");

        register.register(userData);
        AuthData authData = login.login(userData);

        assertEquals("Scott", authData.username());
    }

    @Test
    public void badLogin() throws DataAccessException {
        ClearApplication clear = new ClearApplication(userDAO, gameDAO, authDAO);
        clear.clearAll();

        Login login = new Login(userDAO, authDAO);

        assertThrows(UnauthorizedException.class, () -> login.login(new UserData("Scott", "ScottTheBoss", "Scott@Scotty.com")));
    }

    @Test
    public void goodLogout() throws DataAccessException, BadRequestException, AlreadyTakenException, UnauthorizedException {
        ClearApplication clear = new ClearApplication(userDAO, gameDAO, authDAO);
        clear.clearAll();

        Logout logout = new Logout(authDAO);
        Login login = new Login(userDAO, authDAO);
        Register register = new Register(userDAO, authDAO);

        UserData userData = new UserData("Timmy", "TimmyLovesCarrots", "TimmyEmail@Email.com");
        register.register(userData);
        AuthData authData = login.login(userData);

        logout.logout(authData.authToken());

        assertNull(authDAO.getAuthData(authData.authToken()));
    }

    @Test
    public void badLogout() throws DataAccessException, BadRequestException, AlreadyTakenException, UnauthorizedException {
        ClearApplication clear = new ClearApplication(userDAO, gameDAO, authDAO);
        clear.clearAll();

        Logout logout = new Logout(authDAO);
        Login login = new Login(userDAO, authDAO);
        Register register = new Register(userDAO, authDAO);

        UserData userData = new UserData("Timmy", "TimmyLovesCarrots", "TimmyEmail@Email.com");
        register.register(userData);
        login.login(userData);
        AuthData authData = new AuthData("", "Scotty");

        assertThrows(UnauthorizedException.class, () -> logout.logout(authData.authToken()));
    }

    @Test
    public void goodCreateGame() throws DataAccessException, BadRequestException, AlreadyTakenException, UnauthorizedException {
        ClearApplication clear = new ClearApplication(userDAO, gameDAO, authDAO);
        clear.clearAll();

        Register register = new Register(userDAO, authDAO);
        Login login = new Login(userDAO, authDAO);
        CreateGame createGame = new CreateGame(authDAO, gameDAO);

        ChessGame chessGame = new ChessGame();
        GameData gameData = new GameData(0, null, null, "TimmysGame", chessGame);
        UserData userData = new UserData("Timmy", "yes", "tim@email.com");

        register.register(userData);
        AuthData authData = login.login(userData);

        GameData game = createGame.createGame(gameData, authData.authToken());

        assertEquals("TimmysGame", game.gameName());
    }

    @Test
    public void badCreateGame() throws DataAccessException, UnauthorizedException, BadRequestException {
        ClearApplication clear = new ClearApplication(userDAO, gameDAO, authDAO);
        clear.clearAll();

        CreateGame createGame = new CreateGame(authDAO, gameDAO);

        GameData gameData = new GameData(0, null, null, "1000$Game", null);
        AuthData authData = new AuthData("fakeToken", "TimmysBeingFramed");

        assertThrows(UnauthorizedException.class, () -> createGame.createGame(gameData, authData.authToken()));
    }

    @Test
    public void goodListGames() throws DataAccessException, UnauthorizedException, BadRequestException, AlreadyTakenException {
        ClearApplication clear = new ClearApplication(userDAO, gameDAO, authDAO);
        clear.clearAll();

        Register register = new Register(userDAO, authDAO);
        Login login = new Login(userDAO, authDAO);
        ListGames listGames = new ListGames(authDAO, gameDAO);
        CreateGame createGame = new CreateGame(authDAO, gameDAO);

        UserData userData = new UserData("Timmy", "yes", "tim@email.com");

        register.register(userData);
        AuthData authData = login.login(userData);
        createGame.createGame(new GameData(0, null, null, "TimmysGame", null), authData.authToken());
        createGame.createGame(new GameData(4, null, null, "HulksGame", null), authData.authToken());

        Collection<GameData> games = listGames.listGames(authData.authToken());

        assertEquals(2, games.size());


    }

    @Test
    public void badListGames() throws DataAccessException, UnauthorizedException {
        ClearApplication clear = new ClearApplication(userDAO, gameDAO, authDAO);
        clear.clearAll();

        ListGames listGames = new ListGames(authDAO, gameDAO);

        assertThrows(UnauthorizedException.class, () -> listGames.listGames(null));

    }

    @Test
    public void goodJoin() throws DataAccessException, BadRequestException, AlreadyTakenException, UnauthorizedException {
        ClearApplication clear = new ClearApplication(userDAO, gameDAO, authDAO);
        clear.clearAll();

        CreateGame createGame = new CreateGame(authDAO, gameDAO);
        Register register = new Register(userDAO, authDAO);
        Login login = new Login(userDAO, authDAO);
        JoinGame joinGame = new JoinGame(authDAO, gameDAO);

        UserData userData = new UserData("Franky", "FrankyIsTheBest", "FrankysSecretEmail@Secret.com");

        register.register(userData);
        AuthData authData = login.login(userData);
        GameData gameData = new GameData(0, null, null, "TimmysGame", null);
        var game = createGame.createGame(gameData, authData.authToken());


        joinGame.join(authData.authToken(), game, "WHITE");

        assertEquals("Franky", gameDAO.getGame(1).whiteUsername());
    }

    @Test
    public void badJoin() throws DataAccessException, UnauthorizedException, BadRequestException, AlreadyTakenException {
        ClearApplication clear = new ClearApplication(userDAO, gameDAO, authDAO);
        clear.clearAll();

        JoinGame joinGame = new JoinGame(authDAO, gameDAO);

        assertThrows(BadRequestException.class, () -> joinGame.join(null, null, null));
    }


}
