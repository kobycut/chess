package exceptions;


public class BadRequestException extends Exception{
    final private int statusCode;
    public BadRequestException(int statusCode) {
        this.statusCode = statusCode;
    }
    public String getMessage() {
        return "bad request";
    }

    public int statusCode() {
        return statusCode;
    }
}