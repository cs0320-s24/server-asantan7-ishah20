package edu.brown.cs.student.main.server.acs;

/**
 * Any object that extends from the ACS interface can be used to get broadband data of any county in
 * any state.
 */
public interface ACS {

  /**
   * Return broadband data for a given county in a given state.
   *
   * @param county - the county for which broadband data should be retrieved
   * @param state - the state in which the county is located
   * @throws Exception if method is unable to retrieve broadband percentage
   */
  BroadbandData getBroadbandData(String state, String county) throws Exception;
}
