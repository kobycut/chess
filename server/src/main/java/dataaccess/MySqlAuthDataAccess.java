package dataaccess;

import dataaccess.exceptions.DataAccessException;
import dataaccess.exceptions.ResponseException;
import model.AuthData;

import java.sql.SQLException;

public class MySqlAuthDataAccess implements AuthDAO{
    public MySqlAuthDataAccess() throws ResponseException, DataAccessException {
        configureDatabase();
    }

    public AuthData createAuth(AuthData authData) throws DataAccessException {
        String authToken = authData.authToken();
        String username = authData.username();
        var statement = "INSERT INTO auth (authToken, username) VALUES (?, ?)";
        try (var preparedStatement = DatabaseManager.getConnection().prepareStatement(statement)) {
            preparedStatement.setString(1, authToken);
            preparedStatement.setString(2, username);
            preparedStatement.executeUpdate();

            //  what goes here?

            return authData;
        }
    };

    public AuthData getAuthData(String authToken) throws DataAccessException {

    };

    public void deleteAuth(AuthData authData) throws DataAccessException {
        var authToken = authData.authToken();
        var statement = "DELETE FROM auth WHERE authToken=?";
        try (var preparedStatement = DatabaseManager.getConnection().prepareStatement(statement)) {
            preparedStatement.setString(1, authToken);
            preparedStatement.executeUpdate();
        }

    };

    public void clearAllAuthTokens() throws DataAccessException {

    };






    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS auth (
            'authToken' string NOT NULL,
            'username' string NOT NULL
            )
 
            """

    };

    private void configureDatabase() throws DataAccessException, ResponseException {
        DatabaseManager.createDatabase();
        try (var conn = DatabaseManager.getConnection()) {
            for (var statement : createStatements) {
                try (var preparedStatement = conn.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
        } catch (SQLException ex) {
            throw new ResponseException(500, String.format("Unable to configure database: %s", ex.getMessage()));
        }
    }
}
