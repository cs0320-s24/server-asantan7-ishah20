package edu.brown.cs.student.main.test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import edu.brown.cs.student.main.server.acs.BroadbandData;
import edu.brown.cs.student.main.server.acs.BroadbandHandler;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import okio.Buffer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import spark.Spark;

public class TestBroadbandHandler {

  private final Type mapStringObject =
      Types.newParameterizedType(Map.class, String.class, Object.class);
  private JsonAdapter<Map<String, Object>> adapter;
  private JsonAdapter<BroadbandData> broadbandDataAdapter;

  @BeforeAll
  public static void setupOnce() {
    Spark.port(0);
    Logger.getLogger("").setLevel(Level.WARNING);
  }

  @BeforeEach
  public void setup() {
    MockACSAPI mockedSource = new MockACSAPI(new BroadbandData(88.5, "12-02-2024"));
    Spark.get("/broadband", new BroadbandHandler(mockedSource, false));
    Spark.awaitInitialization();

    Moshi moshi = new Moshi.Builder().build();
    adapter = moshi.adapter(mapStringObject);
    broadbandDataAdapter = moshi.adapter(BroadbandData.class);
  }

  @AfterEach
  public void tearDown() {
    Spark.stop();
    Spark.awaitStop();
  }

  private HttpURLConnection tryRequest(String apiCall) throws IOException {
    URL requestURL = new URL("http://localhost:" + Spark.port() + "/" + apiCall);
    HttpURLConnection connection = (HttpURLConnection) requestURL.openConnection();
    connection.setRequestProperty("Content-Type", "application/json");
    connection.setRequestProperty("Accept", "application/json");
    connection.connect();
    return connection;
  }

  @Test
  public void testBroadbandRequestSuccess() throws IOException {
    HttpURLConnection connection = tryRequest("broadband?state=NY&county=Albany");
    assertEquals(200, connection.getResponseCode());

    Map<String, Object> responseBody =
        adapter.fromJson(new Buffer().readFrom(connection.getInputStream()));
    assertEquals("success", responseBody.get("status"));

    BroadbandData expectedData = new BroadbandData(88.5, "12-02-2024");
    assertEquals(broadbandDataAdapter.toJson(expectedData), responseBody.get("data"));

    connection.disconnect();
  }
}
