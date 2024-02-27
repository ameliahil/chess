package server;

import server.handlers.ClearHandler;
import spark.*;

public class Server {

    //initialize DAOs here, pass into handler

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.
        // Spark, classroom notes
        Spark.delete("/db", (req,res) -> (new ClearHandler()));
        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}
