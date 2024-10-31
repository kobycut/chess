package dataaccess;

import dataaccess.exceptions.DataAccessException;
import model.UserData;
import java.sql.SQLException;

public class MySqlUserDataAccess implements UserDAO {

    public void configure() throws DataAccessException {
        configureDatabase();
    }

    public UserData getUser(String username) throws DataAccessException {
        configure();
        var statement = "SELECT * FROM user WHERE username=?";
        try (var conn = DatabaseManager.getConnection();
             var preparedStatement = conn.prepareStatement(statement)) {
            preparedStatement.setString(1, username);
            var rs = preparedStatement.executeQuery();
            if (rs.next()) {
                var password = rs.getString("password");
                var email = rs.getString("email");
                conn.close();
                return new UserData(username, password, email);
            }
            conn.close();
            return null;
        } catch (SQLException e) {
            throw new DataAccessException(500, e.getMessage());
        }
    }

    public void createUser(UserData userData) throws DataAccessException {
        configure();
        var statement = "INSERT INTO user (username, password, email) VALUES (?,?,?)";
        try (var conn = DatabaseManager.getConnection();
                var preparedStatement = conn.prepareStatement(statement)) {
            preparedStatement.setString(1, userData.username());
            preparedStatement.setString(2, userData.password());
            preparedStatement.setString(3, userData.email());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException(500, e.getMessage());
        }
    }

    ;

    public void clearAllUsers() throws DataAccessException {
        configure();
        var statement = "TRUNCATE user";
        try (var conn = DatabaseManager.getConnection();
                var preparedStatement = conn.prepareStatement(statement)) {
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException(500, e.getMessage());
        }
    }

    ;

    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS user (
            username VARCHAR(255) NOT NULL PRIMARY KEY,
            password VARCHAR(255) NOT NULL,
            email VARCHAR(255)
            )
 
            """

    };

    private void configureDatabase() throws DataAccessException {
        DatabaseManager.createDatabase();
        try (var conn = DatabaseManager.getConnection()) {
            for (var statement : createStatements) {
                try (var preparedStatement = conn.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
        } catch (SQLException ex) {
            throw new DataAccessException(500, String.format("Unable to configure database: %s", ex.getMessage()));
        }
    }

}
