package dataaccess.exceptions;

public class UnauthorizedException extends Exception{
    final private int statusCode;
    public UnauthorizedException(int statusCode, String message) {
        super(message);
        this.statusCode = statusCode;
    }

    public int StatusCode() {
        return statusCode;
    }
}
