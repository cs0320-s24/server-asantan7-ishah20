package edu.brown.cs.student.main.server;
import edu.brown.cs.student.main.server.handler.LoadCSVHandler;
import edu.brown.cs.student.main.server.handler.SearchCSVHandler;
import edu.brown.cs.student.main.server.handler.ViewCSVHandler;
import edu.brown.cs.student.main.server.acs.BroadbandHandler;
import edu.brown.cs.student.main.server.acs.ACSAPI;

import spark.Spark;
import static spark.Spark.after;

/**
 * Contains the main() method which starts Spark and runs the various
 * handlers.
 */
public class Server {
    public static void main(String[] args) {
        int port = 2412;
        Spark.port(port);

        after(
                (request, response) -> {
                    response.header("Access-Control-Allow-Origin", "*");
                    response.header("Access-Control-Allow-Methods", "*");
                });

        // Setting up the handler for the GET endpoints
        LoadCSVHandler loadHandler = new LoadCSVHandler();

        Spark.get("loadCSV", loadHandler);
        Spark.get("viewCSV", new ViewCSVHandler(loadHandler));
        Spark.get("searchCSV", new SearchCSVHandler(loadHandler));
        Spark.get("broadband", new BroadbandHandler(new ACSAPI(), true));

        Spark.init();
        Spark.awaitInitialization();

        System.out.println("Server started at http://localhost:" + port);
    }
}
