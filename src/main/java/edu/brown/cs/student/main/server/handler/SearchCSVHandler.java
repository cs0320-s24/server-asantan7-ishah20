package edu.brown.cs.student.main.server.handler;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import edu.brown.cs.student.main.csv.CSVSearch;
import edu.brown.cs.student.main.csv.CreatorFromRowExamples.SimpleCreator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import spark.Request;
import spark.Response;
import spark.Route;

/**
 * Handler class for the searchCSV API endpoint. This class is responsible for searching a CSV file.
 */
public class SearchCSVHandler implements Route {
  private List<List<String>> csvData;
  private LoadCSVHandler loadCSVHandler;

  /**
   * Constructor for the SearchCSVHandler class.
   *
   * @param loadHandler - Takes in an object from the LoadCSVHandler class (that initially loads the
   *     CSV)
   */
  public SearchCSVHandler(LoadCSVHandler loadHandler) {
    this.loadCSVHandler = loadHandler;
  }

  /**
   * Handles the incoming request to search the loaded CSV file. If no CSV file has been loaded, it
   * returns an error response. Otherwise, it searches the CSV data.
   *
   * @param request The HTTP request object.
   * @param response The HTTP response object used to modify the response properties.
   * @return A JSON formatted string with the outcome of the operation.
   * @throws Exception if there's an issue serializing the response.
   */
  @Override
  public Object handle(Request request, Response response) throws Exception {

    String searchValue = request.queryParams("value");
    String columnIdentifier = request.queryParams("column");
    CSVSearch csvSearch = new CSVSearch(this.loadCSVHandler.parser, new SimpleCreator());

    // Check if the CSV has been previously loaded
    this.csvData = this.loadCSVHandler.getParsedCSVData();
    if (this.csvData == null) {
      return serializeFailureResponse("No CSV file loaded.");
    }

    if (searchValue != null) {
      try {
        Object columnId = null; // Default to null
        if (columnIdentifier != null) {
          try {
            // Trying to convert the column identifier into integer
            columnId = Integer.parseInt(columnIdentifier);
          } catch (NumberFormatException e) {
            // If the column identifier is not integer, treat it as a string
            columnId = columnIdentifier;
          }
        }
        // Performing search
        List<List<String>> results =
            csvSearch.searchListOfString(this.csvData, searchValue, columnId);
        if (results.isEmpty()) {
          return serializeFailureResponse("No results found.");
        } else {
          return serializeSuccessResponse(results);
        }
      } catch (Exception e) {
        return serializeFailureResponse("Search failed: " + e.getMessage());
      }
    } else {
      return serializeFailureResponse("Search failed: Missing search value.");
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
   * Serializes the success response into a JSON formatted string.
   *
   * @param data - a list of rows (represented as list of strings) from the CSV that contain the
   *     search value.
   * @return A JSON string indicating the successful load of the CSV file and the rows containing
   *     the search value.
   */
  private String serializeSuccessResponse(List<List<String>> data) {
    // Creating a hashmap to store the results of the request
    Map<String, Object> responseMap = new HashMap<>();

    // Adding results to the responseMap
    if (data.isEmpty()) {
      responseMap.put("status", "success");
      responseMap.put("message", "No results found for the search criteria.");
    } else {
      responseMap.put("status", "success");
      responseMap.put("data", data);
    }

    // Converting the map to a JSON formatted string using Moshi library
    try {
      Moshi moshi = new Moshi.Builder().build();
      JsonAdapter<Map> jsonAdapter = moshi.adapter(Map.class);
      return jsonAdapter.toJson(responseMap);
    } catch (Exception e) {
      e.printStackTrace();
      responseMap.put("result", "error_serializing_response");
      throw e;
    }
  }
}
