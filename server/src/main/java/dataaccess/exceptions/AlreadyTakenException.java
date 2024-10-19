package dataaccess.exceptions;

public class AlreadyTakenException extends Exception{
    final private int statusCode;
    public AlreadyTakenException(int statusCode, String message) {
        super(message);
        this.statusCode = statusCode;
    }

    public int StatusCode() {
        return statusCode;
    }
}