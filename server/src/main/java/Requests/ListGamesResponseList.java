package Requests;

import java.util.Collection;

public record ListGamesResponseList(Collection<ListGamesResponse> games) {
    public Collection<ListGamesResponse> returnList(){
        return games;
    }
}
