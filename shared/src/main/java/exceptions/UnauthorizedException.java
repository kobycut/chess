package dataaccess.exceptions;

public class UnauthorizedException extends Exception{
    final private int statusCode;
    public UnauthorizedException(int statusCode) {
        this.statusCode = statusCode;
    }
    public String getMessage() {
        return "unauthorized";
    }

    public int statusCode() {
        return statusCode;
    }
}
