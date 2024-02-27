package model;

import com.google.gson.*;

public record AuthData(String authToken, String username) {
    public String toString(){
        return new Gson().toJson(this);
    }
}
