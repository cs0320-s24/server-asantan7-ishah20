package edu.brown.cs.student.main.server.handler;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import edu.brown.cs.student.main.csv.CSVParser;
import edu.brown.cs.student.main.csv.CreatorFromRowExamples.SimpleCreator;
import edu.brown.cs.student.main.csv.Exceptions.FactoryFailureException;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import spark.Request;
import spark.Response;
import spark.Route;

/** Handler class for loading a CSV. Takes a file path. */
public class LoadCSVHandler implements Route {
  private static final String BASE_DIRECTORY = "data/"; // Base directory for CSV files
  private List<List<String>> parsedCSVData; // Parsed CSV data
  public CSVParser parser;

  /**
   * Handles the incoming request to load a CSV file. Validates the request parameters, reads and
   * parses the CSV file, and returns the response.
   *
   * @param request - The request object providing information about the HTTP request
   * @param response - The response object providing functionality for modifying the response
   * @throws Exception -
   */
  @Override
  public Object handle(Request request, Response response) throws Exception {
    String filepath = request.queryParams("filepath");

    if (filepath == null) {
      return serializeFailureResponse("no_filepath_provided");
    }

    String csvFilePath = BASE_DIRECTORY + filepath;

    try (FileReader fileReader = new FileReader(csvFilePath)) {
      boolean hasHeaders = Boolean.parseBoolean(request.queryParams("hasHeaders"));

      // Creating CSVParser
      CSVParser<List<String>> parser = new CSVParser<>(fileReader, new SimpleCreator(), hasHeaders);
      this.parser = parser;

      // Compiling all the parsed data into a List<List<String>>
      this.parsedCSVData = new ArrayList<>();
      List<String> row;
      while ((row = parser.parseNextRow()) != null) {
        this.parsedCSVData.add(row);
      }
      return serializeSuccessResponse(filepath);
    } catch (FileNotFoundException e) {
      return this.serializeFailureResponse("error_file_not_found");
    } catch (IOException | FactoryFailureException e) {
      return this.serializeFailureResponse("error_parsing_csv");
    }
  }

  /**
   * Serializes the success response into a JSON formatted string.
   *
   * @param filepath The file path of the successfully loaded CSV file.
   * @return A JSON string indicating the successful load of the CSV file.
   */
  public String serializeSuccessResponse(String filepath) {
    // Creating a hashmap to store the results of the request
    Map<String, Object> responseMap = new HashMap<>();

    // Adding results to the responseMap
    responseMap.put("result", "success");
    responseMap.put("file", filepath);

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

  /**
   * Serializes the failure response into a JSON formatted string.
   *
   * @param errorMessage The error message to be included in the response.
   * @return A JSON string indicating the failure reason.
   */
  public Object serializeFailureResponse(String errorMessage) {
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
   * Getter method for the parsedCSVData field
   *
   * @return - the parsedCSVData field.
   */
  public List<List<String>> getParsedCSVData() {
    return this.parsedCSVData;
  }
}
