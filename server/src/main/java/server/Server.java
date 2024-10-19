package server;

import com.google.gson.Gson;
import dataaccess.*;
import model.AuthData;
import model.GameData;
import model.UserData;
import spark.*;
import service.*;

import java.util.Collection;

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
    private String registerUser(Request req, Response res) throws DataAccessException {
        UserData userData = new Gson().fromJson(req.body(), UserData.class);
        AuthData authData = registerService.register(userData);

        res.status(200);
        res.body(new Gson().toJson(authData));

        return new Gson().toJson(authData);

    }
    private String login(Request req, Response res) throws DataAccessException {
        UserData userData = new Gson().fromJson(req.body(), UserData.class);
        AuthData authData = loginService.login(userData);

        res.status(200);
        res.body(new Gson().toJson(authData));

        return new Gson().toJson(authData);
    }
    private String logout(Request req, Response res) throws DataAccessException {
        String authToken = req.headers("authorization");

//        AuthData authData = new Gson().fromJson(req.headers("authorization"), AuthData.class);
//        authToken = authData.authToken();

        logoutService.logout(authToken);

        res.status(200);
        res.body("{}");

        return("{}");
    }
    private String listGames(Request req, Response res) throws DataAccessException {
        String authToken = req.headers("authorization");

        Collection<GameData> allGames = listGamesService.listGames(authToken);

        res.status(200);
        res.body(new Gson().toJson(allGames));

        return new Gson().toJson(allGames);
    }
    private String createGame(Request req, Response res) throws DataAccessException {
        String authToken = req.headers("authorization");
        GameData gameData = new Gson().fromJson(req.body(), GameData.class);

        GameData game = createGameService.createGame(gameData, authToken);

        res.status(200);
        res.body(new Gson().toJson(game));

        return (new Gson().toJson(game));
    }
    private String joinGame(Request req, Response res) throws DataAccessException {
        String authToken = req.headers("authorization");
        GameData gameData = new Gson().fromJson(req.body(), GameData.class);

        joinGameService.join(authToken, gameData);

        res.status(200);
        res.body("{}");

        return("{}");
    }
    private String clearApplication(Request req, Response res) throws DataAccessException {
        clearService.clearAll();
        res.status(200);
        res.body("{}");

        return("{}");
    }
}
