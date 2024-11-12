package dataaccess.exceptions;

public class AlreadyTakenException extends Exception{
    final private int statusCode;
    public AlreadyTakenException(int statusCode) {
        this.statusCode = statusCode;
    }
    public String getMessage() {
        return "already taken";
    }

    public int statusCode() {
        return statusCode;
    }
}