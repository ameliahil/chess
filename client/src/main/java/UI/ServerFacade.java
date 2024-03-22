package UI;

import Requests.*;
import com.google.gson.Gson;
import dataAccess.DataAccessException;
import model.UserData;

import java.io.*;
import java.net.*;

public class ServerFacade {
    private final String serverUrl;
    private static String authToken;

    public ServerFacade(String url) {
        serverUrl = url;
    }


    public void clear() throws DataAccessException {
        var path = "/db";
        this.makeRequest("DELETE", path, null, null);
    }

    public LoginResponse registration(UserData user) throws DataAccessException {
        var path = "/user";
        LoginResponse loginResponse = this.makeRequest("POST", path, user, LoginResponse.class);
        authToken = loginResponse.authToken();
        return loginResponse;
    }

    public LoginResponse login(LoginRequest loginRequest) throws DataAccessException {
        var path = "/session";
        LoginResponse loginResponse = this.makeRequest("POST", path, loginRequest, LoginResponse.class);
        authToken = loginResponse.authToken();
        return loginResponse;
    }
    public void logout() throws DataAccessException {
        var path = "/session";
        this.makeRequest("DELETE", path, authToken, null);
    }
    public ListGamesResponseList listGames() throws DataAccessException {
        var path = "/game";
        return this.makeRequest("GET", path, null, ListGamesResponseList.class);
    }
    public CreateGameResponse createGame(CreateGameRequest gameRequest) throws DataAccessException {
        var path = "/game";
        return this.makeRequest("POST", path, gameRequest, CreateGameResponse.class);
    }
    public void joinGame(JoinRequest joinRequest) throws DataAccessException {
        var path = "/game";
        this.makeRequest("PUT", path, joinRequest, null);
    }

    private <T> T makeRequest(String method, String path, Object request, Class<T> responseClass) throws DataAccessException {
        try {
            URL url = (new URI(serverUrl + path)).toURL();
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod(method);
            //if(!method.equals("GET")){
                http.setDoOutput(true);
            //}

            writeBody(request, http);
            http.connect();
            throwIfNotSuccessful(http);
            return readBody(http, responseClass);
        } catch (Exception ex) {
            throw new DataAccessException(ex.getMessage());
        }
    }

    private static void writeBody(Object request, HttpURLConnection http) throws IOException {
        if (authToken != null) {
            http.addRequestProperty("Authorization", authToken);
        }
        if (request != null) {
            http.addRequestProperty("Content-Type", "application/json");
            String reqData = new Gson().toJson(request);
            try (OutputStream reqBody = http.getOutputStream()) {
                reqBody.write(reqData.getBytes());
            }
        }
    }

    private void throwIfNotSuccessful(HttpURLConnection http) throws IOException, DataAccessException {
        var status = http.getResponseCode();
        if (!isSuccessful(status)) {
            throw new DataAccessException("failure: " + status);
        }
    }

    private boolean isSuccessful(int status) {
        return status == 200;
    }

    private static <T> T readBody(HttpURLConnection http, Class<T> responseClass) throws IOException {
        T response = null;
        if (http.getContentLength() < 0) {
            try (InputStream respBody = http.getInputStream()) {
                InputStreamReader reader = new InputStreamReader(respBody);
                if (responseClass != null) {
                    response = new Gson().fromJson(reader, responseClass);
                }
            }
        }
        return response;
    }




}
