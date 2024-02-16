package edu.brown.cs.student.main.test;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Types;
import edu.brown.cs.student.main.server.acs.BroadbandData;
import edu.brown.cs.student.main.server.acs.BroadbandHandler;
import edu.brown.cs.student.main.server.DatasourceException;

import okio.Buffer;
import org.junit.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import spark.Spark;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.testng.AssertJUnit.assertEquals;

public class TestBroadbandHandler {

  @BeforeAll
  public static void setupOnce() {
    Spark.port(0);
    Logger.getLogger("").setLevel(Level.WARNING);
  }

  @BeforeEach
  public void setup() throws DatasourceException {
    Spark.get(
        "/broadband", new BroadbandHandler(new MockACSAPI(new BroadbandData(88.5, "hey")), true));
    Spark.init();
    Spark.awaitInitialization();
  }

    @AfterEach
    public void tearDown() {
        Spark.unmap("/broadband");
        Spark.awaitStop();
    }


    private HttpURLConnection tryRequest(String apiCall) throws IOException {
        // Configure the connection (but don't actually send a request yet)
        URL requestURL = new URL("http://localhost:" + Spark.port() + "/" + apiCall);
        HttpURLConnection clientConnection = (HttpURLConnection) requestURL.openConnection();
        // The request body contains a Json object
        clientConnection.setRequestProperty("Content-Type", "application/json");
        // We're expecting a Json object in the response body
        clientConnection.setRequestProperty("Accept", "application/json");

        clientConnection.connect();
        return clientConnection;
    }

    private final Type mapStringObject = Types.newParameterizedType(Map.class, String.class, Object.class);
    private JsonAdapter<Map<String, Object>> adapter;
    private JsonAdapter<BroadbandData> broadbandDataAdapter;

    @Test
    public void testRequestSuccess() throws IOException {
        // localhost:2412/broadband?state=California&county=ButteCounty
        // Set up the request, make the request
        HttpURLConnection loadConnection = tryRequest("state=California&county=Butte");
        // Get an OK response (the *connection* worked, the *API* provides an error response)
        assertEquals(200, loadConnection.getResponseCode());
        // Get the expected response: a success
        Map<String, Object> responseBody = adapter.fromJson(new Buffer().readFrom(loadConnection.getInputStream()));
        assertEquals("success", responseBody.get("type"));

        // Mocked data: correct temp? We know what it is, because we mocked.
        assertEquals(
                broadbandDataAdapter.toJson(new BroadbandData(88.5, "2024-02-16 01:24:52")),
                responseBody.get("broadbandPercentage"));
        // Notice we had to do something strange above, because the map is
        // from String to *Object*. Awkward testing caused by poor API design...

        loadConnection.disconnect();
    }
}
