package dataAccess;

import com.google.gson.Gson;

public class ExceptionHandler {
    private final String message;

    public ExceptionHandler(String message){
        this.message = message;
    }

    public int findException(){
        return switch (message) {
            case "Error: bad request" -> 400;
            case "Error: unauthorized" -> 401;
            case "Error: already taken" -> 403;
            default -> 500;
        };
    }

}
