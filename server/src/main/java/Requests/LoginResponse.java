package Requests;

import com.google.gson.Gson;

public record LoginResponse(String username, String authToken) {
    public String toString() {
        return new Gson().toJson(this);
    }
}
