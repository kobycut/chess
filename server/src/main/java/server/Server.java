package server;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import dataaccess.*;
import dataaccess.exceptions.*;
import jdk.jshell.spi.ExecutionControlProvider;
import model.AuthData;
import model.GameData;
import model.UserData;
import spark.*;
import service.*;

import java.util.Collection;
import java.util.Map;
import java.util.HashMap;

public class Server {

    private final GameDAO gameDAO = new GameMemoryDataAccess();
    private final AuthDAO authDAO = new AuthMemoryDataAccess();
    private final UserDAO userDAO = new UserMemoryDataAccess();

    private final Register registerService = new Register(userDAO, authDAO);
    private final Login loginService = new Login(userDAO, authDAO);
    private final Logout logoutService = new Logout(authDAO);
    private final ListGames listGamesService = new ListGames(authDAO, gameDAO);
    private final CreateGame createGameService = new CreateGame(authDAO, gameDAO);
    private final JoinGame joinGameService = new JoinGame(authDAO, gameDAO);
    private final ClearApplication clearService = new ClearApplication(userDAO, gameDAO, authDAO);

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        Spark.post("/user", this::registerUser);
        Spark.post("/session", this::login);
        Spark.delete("/session", this::logout);
        Spark.get("/game", this::listGames);
        Spark.post("/game", this::createGame);
        Spark.put("/game", this::joinGame);
        Spark.delete("/db", this::clearApplication);
        Spark.exception(DataAccessException.class, this::dataAccessExceptionHandler);
        Spark.exception(AlreadyTakenException.class, this::alreadyTakenExceptionHandler);
        Spark.exception(BadRequestException.class, this::badRequestExceptionHandler);
        Spark.exception(UnauthorizedException.class, this::unauthorizedExceptionHandler);

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

    private void dataAccessExceptionHandler(DataAccessException ex, Request req, Response res) {
        exceptionHandler(ex, req, res);
        res.status(500);
    }

    private void alreadyTakenExceptionHandler(AlreadyTakenException ex, Request req, Response res) {
        exceptionHandler(ex, req, res);
        res.status(ex.statusCode());
    }

    private void badRequestExceptionHandler(BadRequestException ex, Request req, Response res) {
        exceptionHandler(ex, req, res);
        res.status(ex.statusCode());
    }

    private void unauthorizedExceptionHandler(UnauthorizedException ex, Request req, Response res) {
        exceptionHandler(ex, req, res);
        res.status(ex.statusCode());
    }

    private void exceptionHandler(Exception ex, Request req, Response res) {
        String body = new Gson().toJson(Map.of("message", String.format("Error: %s", ex.getMessage()), "success", false));
        res.type("application/json");
        res.body(body);
    }

    private String registerUser(Request req, Response res) throws AlreadyTakenException, DataAccessException, BadRequestException {
        UserData userData = new Gson().fromJson(req.body(), UserData.class);
        AuthData authData = registerService.register(userData);

        res.status(200);
        res.body(new Gson().toJson(authData));

        return new Gson().toJson(authData);

    }

    private String login(Request req, Response res) throws UnauthorizedException, DataAccessException {
        UserData userData = new Gson().fromJson(req.body(), UserData.class);
        AuthData authData = loginService.login(userData);

        res.status(200);
        res.body(new Gson().toJson(authData));

        return new Gson().toJson(authData);
    }

    private String logout(Request req, Response res) throws UnauthorizedException, DataAccessException {
        String authToken = req.headers("authorization");

        logoutService.logout(authToken);
        res.status(200);
        res.body("{}");

        return ("{}");
    }

    private Object listGames(Request req, Response res) throws UnauthorizedException, DataAccessException {
        String authToken = req.headers("authorization");

        Collection<GameData> allGames = listGamesService.listGames(authToken);
        Map<String, Object> gamesObject = new HashMap<>();
        gamesObject.put("games", allGames);

        res.status(200);
        res.body(new Gson().toJson(gamesObject));
        return new Gson().toJson(gamesObject);
    }

    private String createGame(Request req, Response res) throws UnauthorizedException, DataAccessException, BadRequestException {
        String authToken = req.headers("authorization");
        GameData gameData = new Gson().fromJson(req.body(), GameData.class);

        GameData game = createGameService.createGame(gameData, authToken);

        res.status(200);
        res.body(new Gson().toJson(game));

        return (new Gson().toJson(game));
    }

    private String joinGame(Request req, Response res) throws UnauthorizedException, DataAccessException, BadRequestException, AlreadyTakenException {
        String playerColor = null;
        String authToken = req.headers("authorization");

        JsonObject obj = new Gson().fromJson(req.body(), JsonObject.class);
        if (obj.get("playerColor") != null) {
            playerColor = obj.get("playerColor").getAsString();
        }

        GameData gameData = new Gson().fromJson(req.body(), GameData.class);


        joinGameService.join(authToken, gameData, playerColor);

        res.status(200);
        res.body("{}");

        return ("{}");
    }

    private String clearApplication(Request req, Response res) throws DataAccessException {
        clearService.clearAll();
        res.status(200);
        res.body("{}");

        return ("{}");
    }
}
