import Facades.*;

import exceptions.DataAccessException;
import org.junit.jupiter.api.*;
import server.Server;


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
    public void sampleTest() {


    }
}
