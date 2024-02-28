package Requests;

public record ListGamesResponse(int GameID, String whiteUsername, String blackUsername, String gameName) {
}
