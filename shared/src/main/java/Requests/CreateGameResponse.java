package Requests;

import com.google.gson.Gson;

public record CreateGameResponse(int gameID) {
    public String toString() {
        return new Gson().toJson(this);
    }
}
