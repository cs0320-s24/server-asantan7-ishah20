package edu.brown.cs.student.main.server.acs;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import java.util.concurrent.TimeUnit;

/** A caching proxy for the ACS API that caches broadband data. */
public class ACSAPICacheProxy implements ACS {
  private final ACS wrappedACSAPI; // The underlying ACS API instance to be wrapped by the cache
  private final LoadingCache<String, BroadbandData>
      cache; // The cache storing broadband data responses

  /**
   * Constructs an ACSAPICacheProxy with a specified cache size and expiration duration.
   *
   * @param toWrap The ACS API instance to wrap with caching functionality.
   * @param maximumSize The maximum number of entries the cache can hold.
   * @param minutesToExpire The duration (in minutes) after which a cache entry expires.
   */
  public ACSAPICacheProxy(ACS toWrap, int maximumSize, int minutesToExpire) {
    this.wrappedACSAPI = toWrap;

    // Initializing the cache with the provided parameters.
    this.cache =
        CacheBuilder.newBuilder()
            .maximumSize(maximumSize)
            .expireAfterWrite(minutesToExpire, TimeUnit.MINUTES)
            .recordStats()
            .build(
                new CacheLoader<String, BroadbandData>() {
                  @Override
                  public BroadbandData load(String key) throws Exception {
                    // Splitting the key to extract state and county names:
                    String[] parts = key.split(", ");
                    return wrappedACSAPI.getBroadbandData(parts[0], parts[1]);
                  }
                });
  }

  /**
   * Retrieves broadband data for a specified state and county, using the cache.
   *
   * @param state The name of the state for which broadband data is requested.
   * @param county The name of the county within the specified state.
   * @return The BroadbandData for the specified state and county.
   * @throws Exception If an error occurs during data retrieval.
   */
  @Override
  public BroadbandData getBroadbandData(String state, String county) throws Exception {
    try {
      return cache.getUnchecked(state + ", " + county);
    } catch (Exception e) {
      throw new Exception(e.getMessage());
    }
  }
}
