package Requests;

import chess.ChessGame;
import com.google.gson.Gson;

public record JoinRequest(ChessGame.TeamColor playerColor, int gameID) {
    public String toString() {
        return new Gson().toJson(this);
    }
}
