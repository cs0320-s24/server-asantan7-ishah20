package edu.brown.cs.student.main.server;
import com.google.gson.Gson;
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

    public static String fetchStateCode(String stateName) throws Exception{
        if (stateCodeCache.containsKey(stateName)) {
            return stateCodeCache.get(stateName);
        }

        String uriString = "https://api.census.gov/data/2010/dec/sf1?get=NAME&for=state:*"
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(uriString))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        List<List<String>> states = gson.fromJson(response.body(), new TypeToken<List<List<String>>>() {}.getType());

        for (List<String> state : states) {
        }
    }
}
