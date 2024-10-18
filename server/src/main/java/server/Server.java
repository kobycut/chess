package server;

import dataaccess.*;
import model.UserData;
import spark.*;
import service.*;

public class Server {

    private final GameDAO gameDAO = new GameMemoryDataAccess();
    private final AuthDAO authDAO = new AuthMemoryDataAccess();
    private final UserDAO userDAO = new UserMemoryDataAccess();

    private final CreateGame createGameService = new CreateGame(authDAO, gameDAO);
    private final JoinGame joinGameService = new JoinGame(authDAO, gameDAO);
    private final ListGames listGamesService = new ListGames(authDAO, gameDAO);
    private final Login loginService = new Login(userDAO, authDAO);
    private final Logout logoutService = new Logout(authDAO);
    private final ClearApplication clearService = new ClearApplication(userDAO, gameDAO, authDAO);

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        Spark.post("/user", this::createUser);
        Spark.post("/session", this::login);
        Spark.delete("/session", this::logout);
        Spark.get("/game", this::listGames);
        Spark.post("/game", this::createGame);
        Spark.put("/game", this::joinGame);
        Spark.delete("/db", this::clearApplication);

        // Register your endpoints and handle exceptions here.

        //This line initializes the server and can be removed once you have a functioning endpoint 
        Spark.init();

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
    private String createUser(Request req, Response res) throws Exception {
        return null;
    }
    private String login(Request req, Response res) throws Exception {
        return null;
    }
    private String logout(Request req, Response res) throws Exception {
        return null;
    }
    private String listGames(Request req, Response res) throws Exception {
        return null;
    }
    private String createGame(Request req, Response res) throws Exception {
        return null;
    }
    private String joinGame(Request req, Response res) throws Exception {
        return null;
    }
    private String clearApplication(Request req, Response res) throws Exception {
        clearService.clearAll();

        return "";
    }
}
