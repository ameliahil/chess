package server;

import dataAccess.*;
import server.handlers.*;
import spark.*;


public class Server {

    SQLUserDAO userDAO = new SQLUserDAO();
    SQLGameDAO gameDAO = new SQLGameDAO();
    SQLAuthDAO authDAO = new SQLAuthDAO();

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.
        // Spark, classroom notes
        Spark.delete("/db", (req, res) -> (new ClearHandler(userDAO,gameDAO,authDAO)).clear(req, res));
        Spark.post("/user",(req, res)-> (new RegistrationHandler(userDAO, authDAO).register(req,res)));
        Spark.post("/session",(req,res)-> (new LoginHandler(userDAO, authDAO).login(req,res)));
        Spark.delete("/session",(req,res)-> (new LogoutHandler(authDAO).logout(req,res)));
        Spark.get("/game", (req,res) -> (new ListGamesHandler(gameDAO, authDAO).listGames(req,res)));
        Spark.post("/game",(req, res)->(new CreateGameHandler(gameDAO, authDAO).createGame(req,res)));
        Spark.put("/game",(req,res)-> (new JoinGameHandler(gameDAO, authDAO).joinGame(req,res)));



        Spark.awaitInitialization();
        return Spark.port();
    }

    public static void main(String[] args){
        Server server = new Server();
        server.run(8080);
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}
