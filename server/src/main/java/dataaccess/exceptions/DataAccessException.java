package dataaccess.exceptions;

/**
 * Indicates there was an error connecting to the database
 */
public class DataAccessException extends Exception{
    final private int statusCode;
    public DataAccessException(int statusCode, String message) {
        super(message);
        this.statusCode = statusCode;
    }
    public String getMessage() {
        return "data access failed";
    }

    public int StatusCode() {
        return statusCode;
    }
}
