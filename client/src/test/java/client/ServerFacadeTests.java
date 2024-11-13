import Facades.*;

import exceptions.DataAccessException;
import model.AuthData;
import org.junit.jupiter.api.*;
import server.Server;

import static org.junit.jupiter.api.Assertions.*;


public class ServerFacadeTests {
    static ServerFacade serverFacade;
    private static Server server;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        serverFacade = new ServerFacade("http://localhost:" + port + "/");
    }

    @BeforeEach
    public void doBeforeEach() throws DataAccessException {
        serverFacade.clearAll();
    }


    @AfterAll
    static void stopServer() {
        server.stop();
    }


    @Test
    public void goodLogin() throws DataAccessException {
        AuthData authData = serverFacade.register("andy", "cool", "email");
        serverFacade.logout(authData);
        AuthData authData1 = serverFacade.login("andy", "cool");
        assertNotSame(authData1, authData);
    }

    @Test
    public void badLogin() throws DataAccessException {
        assertThrows(DataAccessException.class, () -> serverFacade.login("", "cool"));
    }

    @Test
    public void goodRegister() throws DataAccessException {
        AuthData authData = serverFacade.register("andy", "cool", "email");
        assert (authData != null);
    }

    @Test
    public void badRegister() throws DataAccessException {
        serverFacade.register("andy", "cool", "email");
        assertThrows(DataAccessException.class, () -> serverFacade.register("andy", "djkd", "dfs"));
    }

    @Test
    public void goodLogout() throws DataAccessException {
        AuthData authData = serverFacade.register("andy", "cool", "email");
        serverFacade.logout(authData);
        AuthData authData2 = serverFacade.login("andy", "cool");
        assert (authData2 != null);
    }

    @Test
    public void badLogout() {
        assertThrows(DataAccessException.class, () -> serverFacade.logout(new AuthData("sdfasdfsaf", "andy")));
    }

    @Test
    public void goodCreateGame() throws DataAccessException {
        AuthData authData = serverFacade.register("andy", "cool", "email");
        serverFacade.createGame("TEST", authData);
        assert (serverFacade.listGames(authData) != null);
    }

    @Test
    public void badCreateGame() {
        assertThrows(DataAccessException.class, () -> serverFacade.createGame("TESTGAME", new AuthData("sdfasdfsaf", "andy")));
    }

    @Test
    public void goodListGames() throws DataAccessException {
        AuthData authData = serverFacade.register("andy", "cool", "email");
        serverFacade.createGame("TEST", authData);
        assert (serverFacade.listGames(authData) != null);
    }

    @Test
    public void badListGames() {
        assertThrows(DataAccessException.class, () -> serverFacade.listGames(new AuthData("sdfasdfsaf", "andy")));


    }

    @Test
    public void goodJoinGame() throws DataAccessException {
        AuthData authData = serverFacade.register("andy", "cool", "email");
        serverFacade.createGame("TEST", authData);
        serverFacade.joinGame(1, "BLACK", authData);
        assert (serverFacade.listGames(authData) != null);


    }

    @Test
    public void badJoinGame() throws DataAccessException {
        AuthData authData = serverFacade.register("andy", "cool", "email");
        serverFacade.createGame("TEST", authData);
        serverFacade.joinGame(1, "BLACK", authData);
        assertThrows(DataAccessException.class, () -> serverFacade.joinGame(1, "BLACK", authData));


    }
}
