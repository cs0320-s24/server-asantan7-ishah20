package edu.brown.cs.student.main.test;

import static org.testng.AssertJUnit.assertEquals;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import edu.brown.cs.student.main.server.handler.LoadCSVHandler;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import okio.Buffer;
import org.junit.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import spark.Spark;

public class TestLoadCSVHandler {

  @BeforeAll
  public static void setupOnce() {
    Spark.port(0);
    Logger.getLogger("").setLevel(Level.WARNING);
  }

  private final Type mapStringObject =
      Types.newParameterizedType(Map.class, String.class, Object.class);
  private JsonAdapter<Map<String, Object>> adapter;

  @BeforeEach
  public void setup() {
    LoadCSVHandler handler = new LoadCSVHandler();
    Spark.get("/loadcsv", handler);
    Spark.awaitInitialization();

    Moshi moshi = new Moshi.Builder().build();
    adapter = moshi.adapter(mapStringObject);
  }

  @AfterEach
  public void tearDown() {
    Spark.unmap("/loadcsv");
    Spark.awaitStop();
  }

  private HttpURLConnection tryRequest(String apiCall) throws IOException, IOException {
    URL requestURL = new URL("http://localhost:" + Spark.port() + "/" + apiCall);
    HttpURLConnection clientConnection = (HttpURLConnection) requestURL.openConnection();
    clientConnection.setRequestProperty("Content-Type", "application/json");
    clientConnection.setRequestProperty("Accept", "application/json");

    clientConnection.connect();
    return clientConnection;
  }

  @Test
  public void testRequestSuccess() throws IOException {
    Spark.port(0);
    Logger.getLogger("").setLevel(Level.WARNING);
    LoadCSVHandler handler = new LoadCSVHandler();
    Spark.get("/loadcsv", handler);
    Spark.awaitInitialization();

    Moshi moshi = new Moshi.Builder().build();
    adapter = moshi.adapter(mapStringObject);
    String validFilepath = "census/dol_ri_earnings_disparity.csv";
    HttpURLConnection loadConnection = tryRequest("filepath=" + validFilepath);
    assertEquals(200, loadConnection.getResponseCode());
    Map<String, Object> body =
        adapter.fromJson(new Buffer().readFrom(loadConnection.getInputStream()));
    showDetailsIfError(body);
    assertEquals("success", body.get("result"));
    assertEquals(validFilepath, body.get("filepath"));
    loadConnection.disconnect();
  }

  private void showDetailsIfError(Map<String, Object> body) {
    if (body.containsKey("result") && "error".equals(body.get("result"))) {
      System.out.println(body.toString());
    }
  }
}
