package model;

import chess.*;
import com.google.gson.Gson;

public record GameData(int GameID, String whiteUsername, String blackUsername, String gameName, ChessGame implementation) {
    public String toString(){
        return new Gson().toJson(this);
    }

}
