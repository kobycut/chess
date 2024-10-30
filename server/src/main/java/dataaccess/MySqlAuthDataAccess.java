package dataaccess;

import dataaccess.exceptions.DataAccessException;
import dataaccess.exceptions.ResponseException;
import model.AuthData;

import javax.xml.crypto.Data;
import java.sql.SQLException;

public class MySqlAuthDataAccess implements AuthDAO {

    private void configure() throws DataAccessException {
        configureDatabase();
    }

    public AuthData createAuth(AuthData authData) throws DataAccessException {
        configure();
        String authToken = authData.authToken();
        String username = authData.username();
        var statement = "INSERT INTO auth (authToken, username) VALUES (?, ?)";
        try (var conn = DatabaseManager.getConnection();
             var preparedStatement = conn.prepareStatement(statement)) {
            preparedStatement.setString(1, authToken);
            preparedStatement.setString(2, username);
            preparedStatement.executeUpdate();
            conn.close();

            return authData;

        } catch (SQLException e) {
            throw new DataAccessException(500, e.getMessage());
        }
    }

    ;

    public AuthData getAuthData(String authToken) throws DataAccessException {
        configure();
        var statement = "SELECT * FROM auth WHERE authToken=?";
        try (var conn = DatabaseManager.getConnection();
             var preparedStatement = conn.prepareStatement(statement)) {
            preparedStatement.setString(1, authToken);
            var rs = preparedStatement.executeQuery();
            if (rs.next()) {
                var username = rs.getString("username");
                conn.close();
                return new AuthData(authToken, username);
            }
            conn.close();
            return null;


        } catch (SQLException e) {
            throw new DataAccessException(500, e.getMessage());
        }
    }

    ;

    public void deleteAuth(AuthData authData) throws DataAccessException {
        configure();
        var authToken = authData.authToken();
        var statement = "DELETE FROM auth WHERE authToken=?";
        try (var conn = DatabaseManager.getConnection();
                var preparedStatement = conn.prepareStatement(statement)) {
            preparedStatement.setString(1, authToken);
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            throw new DataAccessException(500, e.getMessage());
        }

    }

    ;

    public void clearAllAuthTokens() throws DataAccessException {
        configure();
        var statement = "TRUNCATE auth";
        try (var conn = DatabaseManager.getConnection();
                var preparedStatement = conn.prepareStatement(statement)) {
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException(500, e.getMessage());
        }
    }

    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS auth (
            authToken VARCHAR(255) NOT NULL PRIMARY KEY,
            username VARCHAR(255) NOT NULL
            )
 
            """

    };

    private void configureDatabase() throws DataAccessException {
        DatabaseManager.createDatabase();
        try (var conn = DatabaseManager.getConnection()) {
            for (var statement : createStatements) {
                try (var preparedStatement = conn.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                    conn.close();
                }
            }
        } catch (SQLException ex) {
            throw new DataAccessException(500, String.format("Unable to configure database: %s", ex.getMessage()));
        }
    }
}
