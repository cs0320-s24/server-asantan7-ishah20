package edu.brown.cs.student.main.server.handler;
import edu.brown.cs.student.main.server.handler.LoadCSVHandler;
import spark.Request;
import spark.Response;
import spark.Route;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Handler for the viewCSV API endpoint.
 * This class is responsible for returning the contents of the loaded CSV file.
 */
public class ViewCSVHandler implements Route {
    private static List<List<String>> csvData;
    private LoadCSVHandler loadHandler;


    /**
     * Constructor for the ViewCSVHandler class
     *
     * @param loadHandler - Object of the LoadCSVHandler class that contains the loaded CSV file.
     */
    public ViewCSVHandler(LoadCSVHandler loadHandler) {
        this.loadHandler = loadHandler;
    }


    /**
     * Handles the incoming request to view the loaded CSV file.
     * If no CSV file has been loaded, it returns an error response.
     * Otherwise, it returns a success response containing the CSV data.
     *
     * @param request  The HTTP request object.
     * @param response The HTTP response object used to modify the response properties.
     * @return A JSON formatted string with the outcome of the operation.
     * @throws Exception if there's an issue serializing the response.
     */
    @Override
    public Object handle(Request request, Response response) throws Exception {
        // Get parsed CSV data (formatted as List<List<String>>)
        this.csvData = this.loadHandler.getParsedCSVData();

        if (this.csvData != null){
            return serializeSuccessResponse(csvData);
        } else {
            return serializeFailureResponse("No CSV file loaded.");
        }
    }


    /**
     * Serializes the failure response into a JSON formatted string.
     *
     * @param errorMessage The error message to be included in the response.
     * @return A JSON string indicating the failure reason.
     */
    private String serializeFailureResponse(String errorMessage) {
        // Creating a hashmap to store the results of the request
        Map<String, Object> responseMap = new HashMap<>();

        // Adding results to the responseMap
        responseMap.put("status", "error");
        responseMap.put("message", errorMessage);

        // Converting a map to a JSON formatted string using Moshi library
        try {
            Moshi moshi = new Moshi.Builder().build();
            JsonAdapter<Map> adapter = moshi.adapter(Map.class);
            return adapter.toJson(responseMap);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }


    /**
     * Serializes the success response, containing the CSV data, into a JSON formatted string.
     *
     * @param data The CSV data to include in the success response.
     * @return A JSON string indicating the successful retrieval of the CSV data.
     */
    private String serializeSuccessResponse(List<List<String>> data) {
        // Creating a hashmap to store the results of the request
        Map<String, Object> responseMap = new HashMap<>();

        // Adding results to the responseMap
        responseMap.put("result", "success");
        responseMap.put("data", data);

        // Converting a map to a JSON formatted string using Moshi library
        try {
            Moshi moshi = new Moshi.Builder().build();
            JsonAdapter<Map> adapter = moshi.adapter(Map.class);
            return adapter.toJson(responseMap);
        } catch (Exception e) {
            e.printStackTrace();
            responseMap.put("result", "error_serializing_response");
            throw e;
        }
    }
}