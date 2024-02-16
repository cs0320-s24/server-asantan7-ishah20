package edu.brown.cs.student.main.server.acs;


import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.brown.cs.student.main.server.DatasourceException;
import okio.Buffer;

/**
 * Retrieves broadband data by interacting with the real Census API.
 */
public class ACSAPI implements ACS {
    // Cache for storing state codes to minimize API calls
    private static final Map<String, String> stateCodeCache = new HashMap<>();


    /**
     * Retrieves the state code for a given state name by querying the census API.
     *
     * @param stateName The name of the state for which the code is requested.
     * @return the state's code
     * @throws Exception If there is an issue with the API connection or request processing.
     */
    public static String getStateCode(String stateName) throws Exception {
        try {
            // Constructing API URL for fetching state codes
            String url = "https://api.census.gov/data/2010/dec/sf1?get=NAME&for=state:*";
            URL requestURL = new URL(url);
            HttpURLConnection clientConnection = connect(requestURL);

            // Setting up JSON parser using Moshi
            Type listOfString = Types.newParameterizedType(List.class, String.class);
            Type listOfListOfString = Types.newParameterizedType(List.class, listOfString);
            Moshi moshi = new Moshi.Builder().build();
            JsonAdapter<List<List<String>>> adapter = moshi.adapter(listOfListOfString);

            List<List<String>> body =
                    adapter.fromJson(new Buffer().readFrom(clientConnection.getInputStream()));

            System.out.println(body);
            clientConnection.disconnect();

            if (stateCodeCache.containsKey(stateName)) {
                return stateCodeCache.get(stateName);
            }

            // Iterating through states and populating stateCodeCache
            for (List<String> state : body) {
                if (state.get(0).equalsIgnoreCase(stateName)) {
                    String stateCode = state.get(1);
                    stateCodeCache.put(stateName, stateCode);
                    return stateCode;
                }
            }
        } catch (IOException e) {
            throw new DatasourceException(e.getMessage(), e);
        }
        throw new DatasourceException("No state matched " + stateName);
    }


    /**
     * Retrieves the county code for a given county name by querying the census API.
     *
     * @param stateCode   The code of the state where the county is located.
     * @param countyName  The name of the county whose code is to be fetched.
     * @return The county code if found.
     * @throws Exception If there's an error during the API call or while processing the request.
     */
    public static String getCountyCode(String stateCode, String countyName) throws Exception {
        try {
            // Constructing API URL for fetching state codes
            String url = "https://api.census.gov/data/2010/dec/sf1?get=NAME&for=county:*&in=state:"
                    + stateCode;
            URL requestURL = new URL(url);
            HttpURLConnection clientConnection = connect(requestURL);

            // Setting up JSON parser using Moshi
            Type listOfString = Types.newParameterizedType(List.class, String.class);
            Type listOfListOfString = Types.newParameterizedType(List.class, listOfString);
            Moshi moshi = new Moshi.Builder().build();
            JsonAdapter<List<List<String>>> adapter = moshi.adapter(listOfListOfString);

            List<List<String>> body =
                    adapter.fromJson(new Buffer().readFrom(clientConnection.getInputStream()));

            System.out.println(body);
            clientConnection.disconnect();

            // Iterating through fetched counties to find the matching county code
            for (List<String> county : body) {
                String[] words = county.get(0).split(" ");
                String firstWord = words[0]; // Get the first word which represents the county's name

                if (firstWord.equalsIgnoreCase(countyName)) {
                    return county.get(2); // Get the county code
                }
            }
        } catch (IOException e) {
            throw new DatasourceException(e.getMessage(), e);
        }
        throw new DatasourceException("No county matched " + countyName);
    }


    /**
     * Retrieves broadband data for a specified state and county by making an API call.
     * This method first retrieves the state and county codes, then uses them to get the broadband data.
     *
     * @param state The name of the state for which broadband data is requested.
     * @param county The name of the county within the specified state.
     * @throws Exception If there's an error in fetching state or county codes, or in retrieving broadband data.
     */
    @Override
    public BroadbandData getBroadbandData(String state, String county) throws Exception {
        // Getting state and county code
        String stateCode = getStateCode(state);
        String countyCode = getCountyCode(stateCode, county);

        if (stateCode == null || countyCode == null) {
            throw new Exception("Error fetching state and/or county codes.");
        }

        try {
            // Constructing the URL for the API call using the state and county codes
            String url =
                    "https://api.census.gov/data/2021/acs/acs1/subject/variables?get=NAME,S2802_C03_022E&for=county:"
                            + countyCode + "&in=state:" + stateCode;

            // Establishing a connection to the API
            URL requestURL = new URL(url);
            HttpURLConnection clientConnection = connect(requestURL);

            Type listOfString = Types.newParameterizedType(List.class, String.class);
            Type listOfListOfString = Types.newParameterizedType(List.class, listOfString);
            Moshi moshi = new Moshi.Builder().build();
            JsonAdapter<List<List<String>>> adapter = moshi.adapter(listOfListOfString);

            // Parsing the JSON response from the API
            List<List<String>> body =
                    adapter.fromJson(new Buffer().readFrom(clientConnection.getInputStream()));
            clientConnection.disconnect();

            // Retrieving the broadband data from the parsed JSON
            List<String> result = body.get(1);
            Double broadbandPercentage = Double.parseDouble((result.get(1)));
            String dateAndTime =
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss"));
            return new BroadbandData(broadbandPercentage, dateAndTime);
        } catch (DatasourceException e) {
            throw new DatasourceException(e.getMessage());
        }
    }


    /**
     * Establishes a connection to the specified URL and handles common setup.
     *
     * @param requestURL URL to connect to.
     * @return An active HTTP connection.
     * @throws DatasourceException if the connection fails.
     */
    private static HttpURLConnection connect(URL requestURL) throws DatasourceException, IOException {
        URLConnection urlConnection = requestURL.openConnection();
        if (!(urlConnection instanceof HttpURLConnection))
            throw new DatasourceException("unexpected: result of connection wasn't HTTP");
        HttpURLConnection clientConnection = (HttpURLConnection) urlConnection;
        clientConnection.connect(); // GET
        if (clientConnection.getResponseCode() != 200)
            throw new DatasourceException(
                    "unexpected: API connection not success status " + clientConnection.getResponseMessage());
        return clientConnection;
    }
}
