package edu.brown.cs.student.main.server;
import spark.Request;
import spark.Response;
import spark.Route;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class BroadbandHandler implements Route{
    /***
     * BroadbandHandler that takes in the request and response
     * @param request
     * @param response
     * @return
     * @throws Exception
     */

    @Override
    public Object handle(Request request, Response response) throws Exception {
        Set<String> params = request.queryParams();

        String state = request.queryParams("state");
        String county = request.queryParams("county");

        Map<String, Object> responseMap = new HashMap<>();
        try {
            String broadbandJson = this.sendRequest(state, county);

            responseMap.put("result", "success");
            responseMap.put("broadbandData", broadbandJson);
            return responseMap;
        } catch (Exception e) {
            e.printStackTrace();
            responseMap.put("result", "Exception");
            responseMap.put("message", "Failed ot fetch or parse broandband data");

        }

        return responseMap;
    }

    /**
     * Sends request to API
     */
    private String sendRequest(String state, String county) throws URISyntaxException, IOException, InterruptedException {
        String uriString = String.format("api.com", state, county);

        java.net.http.HttpRequest request =
                HttpRequest.newBuilder()
                        .uri(new URI("http://www.boredapi.com/api/activity/"))
                        .GET()
                        .build();

        HttpResponse<String> response = HttpClient.newHttpClient()
                .send(request, HttpResponse.BodyHandlers.ofString());

        return response.body();

    }
}

