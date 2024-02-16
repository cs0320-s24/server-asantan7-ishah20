package edu.brown.cs.student.main.server.acs;

/**
 * A record containing the information we want to retain from the ACS data and provide to our
 * client.
 */
public record BroadbandData(double broadbandPercentage, String dateTime) {}
