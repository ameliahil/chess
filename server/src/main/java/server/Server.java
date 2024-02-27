package server;

import dataAccess.*;
import server.handlers.ClearHandler;
import server.handlers.RegistrationHandler;
import spark.*;


public class Server {

    MemoryUserDAO userDAO = new MemoryUserDAO();
    MemoryGameDAO gameDAO = new MemoryGameDAO();
    MemoryAuthDAO authDAO = new MemoryAuthDAO();

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.
        // Spark, classroom notes
        Spark.delete("/db", (Request req, Response res) -> (new ClearHandler(userDAO,gameDAO,authDAO)).clear(req, res));
        Spark.post("/user",(req, res)-> (new RegistrationHandler(userDAO).register(req,res)));

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
