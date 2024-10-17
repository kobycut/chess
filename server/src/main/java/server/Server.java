package server;

import dataaccess.DataAccess;
import dataaccess.MemoryDataAccess;
import model.UserData;
import spark.*;
import service.*;

public class Server {
    private final MemoryDataAccess memoryDataAccess = new MemoryDataAccess();

    private final CreateGame createGameService = new CreateGame();
    private final JoinGame joinGameService = new JoinGame();
    private final ListGames listGamesService = new ListGames();
    private final Login loginService = new Login();
    private final Logout logoutService = new Logout();
    private final ClearApplication clearService = new ClearApplication();

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
        clearService.clearAllAuthTokens();
        clearService.clearAllGames();
        clearService.clearAllUsers();

        return "";
    }
}
