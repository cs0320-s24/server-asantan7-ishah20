package edu.brown.cs.student.main.server;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CensusAPIHelper {

    /**
     * Uses the API to return state code and county code.
     */
    private static final HttpClient httpClient = HttpClient.newHttpClient();
    private static final Gson gson = new Gson();
    private static final Map<String, String> stateCodeCache = new HashMap<>();

    /**
     * Method that fetches the code for the state
     */
    public static String fetchStateCode(String stateName) throws Exception{
        if (stateCodeCache.containsKey(stateName)) {
            return stateCodeCache.get(stateName);
        }

        String uriString = "https://api.census.gov/data/2010/dec/sf1?get=NAME&for=state:*";
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(uriString))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        List<List<String>> states = gson.fromJson(response.body(), new TypeToken<List<List<String>>>() {}.getType());

        for (List<String> state : states) {
            if (state.get(0).equalsIgnoreCase(stateName)) {
                String stateCode = state.get(1).split(":")[1];
                stateCodeCache.put(stateName, stateCode);
                return stateCode;
            }
        }
        return null;
    }

    public static String fetchCountyCode(String stateName, String countyName) throws Exception{
        String stateCode = fetchStateCode(stateName);
        if (stateCode == null) {
            return null;
        }

        String uriString = String.format("https://api.census.gov/data/2010/dec/sf1?get=NAME&for=county:*&in=state:%s", stateCode);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(uriString))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        List<List<String>> counties = gson.fromJson(response.body(), new TypeToken<List<List<String>>>() {}.getType());

        for (List<String> county : counties){
            if (county.get(0).equalsIgnoreCase(countyName + ", " + stateName)) {
                String countyCode = county.get(2).split(":")[1];
                return countyCode;
            }
        }
        return null;
    }

    /**
     * Method that fetches the code for the county
     */
}
