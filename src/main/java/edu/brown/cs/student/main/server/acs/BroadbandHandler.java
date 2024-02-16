package edu.brown.cs.student.main.server.acs;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import java.lang.reflect.Type;
import java.util.*;
import spark.Request;
import spark.Response;
import spark.Route;

/** Handles a request to the server by making a request to the ACS server. */
public class BroadbandHandler implements Route {
  private final ACS datasource;

  /**
   * Constructs a BroadbandHandler with optional caching.
   *
   * @param source The ACS data source.
   * @param cache Whether to use caching. If true, an ACSAPICacheProxy is used. The developer may
   *     specify the parameters.
   */
  public BroadbandHandler(ACS source, Boolean cache) {
    if (cache) {
      this.datasource = new ACSAPICacheProxy(source, 1000, 10);
    } else {
      this.datasource = source;
    }
  }

  /**
   * Handles an incoming request for broadband data by fetching data from the ACS API based on state
   * and county parameters.
   *
   * @param request The Spark request object containing query parameters.
   * @param response The Spark response object for modifying the response properties.
   * @return A JSON string representing either the fetched broadband data or an error message.
   */
  @Override
  public Object handle(Request request, Response response) {
    // Parsing the state and county from the request parameters
    String state = request.queryParams("state");
    String county = request.queryParams("county");

    if (state == null || county == null) {
      return BroadbandFailureResponse("Required parameters missing: state and/or county");
    }

    try {
      // Retrieving the broadband data for the given state and county
      BroadbandData data = datasource.getBroadbandData(state, county);
      return BroadbandSuccessResponse(data);
    } catch (Exception e) {
      return BroadbandFailureResponse(
          "Failed to retrieve data for given state and county: " + e.getMessage());
    }
  }

  /**
   * Creates a failure response JSON string for when an error occurs.
   *
   * @param errorMessage The error message to include in the response.
   * @return A JSON string indicating an error, including the provided message.
   */
  public String BroadbandFailureResponse(String errorMessage) {
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
   * Creates a success response JSON string containing the broadband data.
   *
   * @param data The BroadbandData object to include in the response.
   * @return A JSON string indicating success and containing the serialized broadband data.
   */
  public String BroadbandSuccessResponse(BroadbandData data) {
    // Creating a hashmap to store the results of the request
    Map<String, Object> responseMap = new HashMap<>();

    // Converting the map to a JSON formatted string using Moshi library
    try {
      Moshi moshi = new Moshi.Builder().build();
      Type mapStringObject = Types.newParameterizedType(Map.class, String.class, Object.class);
      JsonAdapter<Map<String, Object>> adapter = moshi.adapter(mapStringObject);
      JsonAdapter<BroadbandData> acsDataAdapter = moshi.adapter(BroadbandData.class);

      // Adding results to the responseMap
      responseMap.put("status", "success");
      responseMap.put("data", acsDataAdapter.toJson(data));

      return adapter.toJson(responseMap);
    } catch (Exception e) {
      e.printStackTrace();
      responseMap.put("result", "error_serializing_response");
      throw e;
    }
  }
}
