package edu.brown.cs.student.main.test;

import edu.brown.cs.student.main.server.handler.LoadCSVHandler;
import edu.brown.cs.student.main.server.handler.SearchCSVHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import spark.Request;
import spark.Response;

public class TestSearchCSVHandler {

    public class SearchCSVHandlerTest {

        private Request mockRequest;
        private Response mockResponse;
        private LoadCSVHandler mockLoadCSVHandler;
        private SearchCSVHandler searchCSVHandler;

        @BeforeEach
        public void setUp(){
            mockRequest = mock(Request.class);
            mockResponse = mock(Response.class);

            mockLoadCSVHandler = mock(LoadCSVHandler.class);
            searchCSVHandler = new SearchCSVHandler(mockLoadCSVHandler);
        }

        @Test
        public void testNoCSV() throws Exception {
            when(mockLoadCSVHandler.getParsedCSVData())
        }
    }
}
