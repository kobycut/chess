package dataaccess.exceptions;


public class BadRequestException extends Exception{
    final private int statusCode;
    public BadRequestException(int statusCode) {
        this.statusCode = statusCode;
    }
    public String getMessage() {
        return "bad request";
    }

    public int StatusCode() {
        return statusCode;
    }
}