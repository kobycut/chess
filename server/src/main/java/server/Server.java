package server;

import com.google.gson.Gson;
import dataaccess.*;
import model.AuthData;
import model.UserData;
import spark.*;
import service.*;

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
    private void registerUser(Request req, Response res) throws Exception {
        UserData user = new Gson().fromJson(req.body(), UserData.class);
        String username = user.username();
        UserData userData = registerService.getUser(username);

        AuthData auth = new Gson().fromJson(req.body(), AuthData.class);
        AuthData authData = registerService.createAuth(auth);

        res.status(200);
        res.body(new Gson().toJson(userData));
        res.body(new Gson().toJson(authData));

    }
    private void login(Request req, Response res) throws Exception {
        UserData user = new Gson().fromJson(req.body(), UserData.class);
        String username = user.username();
        UserData userData = loginService.getUserData(username);

        AuthData auth = new Gson().fromJson(req.body(), AuthData.class);
        AuthData authData = loginService.createAuth(auth.username());

        res.status(200);
        res.body(new Gson().toJson(userData));
        res.body(new Gson().toJson(authData));
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
    private void clearApplication(Request req, Response res){
        clearService.clearAll();
        res.status(200);
        res.body("{}");
    }
}
