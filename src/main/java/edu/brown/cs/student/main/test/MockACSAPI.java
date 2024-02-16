package edu.brown.cs.student.main.test;

import edu.brown.cs.student.main.server.acs.ACS;
import edu.brown.cs.student.main.server.acs.BroadbandData;

/**
 * A datasource that never actually calls the ACS API, but always returns a constant broadband
 * percentage value.
 */
public class MockACSAPI implements ACS {
    private final BroadbandData constantData;

    public MockACSAPI(BroadbandData constantData) {
        this.constantData = constantData;
    }

    /**
     * Method that returns constant broadband percentage for testing.
     *
     * @param state - name of the county's state
     * @param county - name of county
     * @return constant broadband percentage
     */
    @Override
    public BroadbandData getBroadbandData(String state, String county) throws Exception {
        return constantData;
    }
}
