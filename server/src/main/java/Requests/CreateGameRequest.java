package Requests;

import com.google.gson.Gson;

public record CreateGameRequest(String gameName) {
    public String toString() {
        return new Gson().toJson(this);
    }
}
