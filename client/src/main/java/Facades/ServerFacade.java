package Facades;


import com.google.gson.Gson;
import model.AuthData;
import model.GameData;
import model.UserData;
import exceptions.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.List;
import java.util.Map;

public class ServerFacade {
    private final String serverUrl;
    private AuthData authData;

    public ServerFacade(String url) {
        serverUrl = url;
    }

    public void observeGame(Integer id) {
    }

    public void login(String username, String password) throws DataAccessException {
        var path = "/session";
        var record = new UserData(username, password, null);
        this.authData = this.makeRequest("POST", path, record, AuthData.class);
    }

    public void register(String username, String password, String email) throws DataAccessException {
        try {
            var path = "/user";
            var record = new UserData(username, password, email);
            this.authData = this.makeRequest("POST", path, record, AuthData.class);
        } catch (Exception ex) {
            throw new DataAccessException(500, "username already taken");
        }

    }

    public String logout() throws DataAccessException {
        var path = "/session";

        var status = this.makeRequest("DELETE", path, authData, String.class);
        this.authData = null;
        return status;

    }

    public void createGame(String param) throws DataAccessException {
        var path = "/game";
        // takes in header "authorization" authToken and body GameData
        GameData game = new GameData(0, null, null, param, null);
        GameAuthObject gameAuthObject = new GameAuthObject(game, authData.authToken());
        this.makeRequest("POST", path, gameAuthObject, GameData.class);
    }

    public Object listGames() throws DataAccessException {
        var path = "/game";
        return this.makeRequest("GET", path, authData, Map.class);
    }

    public void joinGame(Integer id, String playerColor) {
    }


    private <T> T makeRequest(String method, String path, Object request, Class<T> responseClass) throws DataAccessException {
        try {
            URI uri = new URI(serverUrl + path);
            HttpURLConnection http = (HttpURLConnection) uri.toURL().openConnection();
            http.setRequestMethod(method);
            http.setDoOutput(true);
            writeBody(request, http);
            http.connect();
            throwIfNotSuccessful(http);
            return readBody(http, responseClass);
        } catch (Exception ex) {
            throw new DataAccessException(500, ex.getMessage());
        }
    }

    private static void writeBody(Object request, HttpURLConnection http) throws IOException {
        if (request != null) {
            if (request instanceof GameAuthObject) {
                GameAuthObject gameAuthObject = (GameAuthObject) request;
                String authToken = gameAuthObject.getAuth();
                http.addRequestProperty("authorization", authToken);
                request = gameAuthObject.getGame();
            }
            http.addRequestProperty("Content-Type", "application/json");
            String reqData = new Gson().toJson(request);
            try (OutputStream reqBody = http.getOutputStream()) {
                reqBody.write(reqData.getBytes());
            }
        }
    }

    private void throwIfNotSuccessful(HttpURLConnection http) throws IOException, DataAccessException {
        var status = http.getResponseCode();
        if (!isSuccessful(status)) {
            throw new DataAccessException(status, "failure: " + status);
        }
    }

    private static <T> T readBody(HttpURLConnection http, Class<T> responseClass) throws IOException {
        T response = null;
        if (http.getContentLength() < 0) {
            try (InputStream respBody = http.getInputStream()) {
                InputStreamReader reader = new InputStreamReader(respBody);
                if (responseClass != null) {
                    response = new Gson().fromJson(reader, responseClass);
                }
            }
        }
        return response;
    }

    private boolean isSuccessful(int status) {
        return status / 100 == 2;
    }

    public class GameAuthObject extends Object {
        private GameData game;
        private String authToken;

        public GameAuthObject(GameData game, String authToken) {
            this.game = game;
            this.authToken = authToken;
        }
        public GameData getGame() {
            return game;
        }
        public String getAuth() {
            return this.authToken;
        }
    }


}
