package server;

public class ServerException extends Exception {

    private int statusCode;
    private String message;

    public ServerException() {
        statusCode = 400;
        message = "server error";
    }

    public ServerException(int statusCode, String message) {
        this.message = message;
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return this.statusCode;
    }

    public String getMessage() {
        return this.message;
    }
    
}