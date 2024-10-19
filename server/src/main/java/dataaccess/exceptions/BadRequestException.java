package dataaccess.exceptions;


public class BadRequestException extends Exception{
    final private int statusCode;
    public BadRequestException(int statusCode, String message) {
        super(message);
        this.statusCode = statusCode;
    }

    public int StatusCode() {
        return statusCode;
    }
}