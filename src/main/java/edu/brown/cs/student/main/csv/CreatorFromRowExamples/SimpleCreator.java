package edu.brown.cs.student.main.csv.CreatorFromRowExamples;

import edu.brown.cs.student.main.csv.CreatorFromRowExamples.CreatorFromRow;
import java.util.List;

public class SimpleCreator implements CreatorFromRow<List<String>> {

  /**
   * Creates a List<String> object from a given CSV row. In this implementation, the input row is
   * directly returned without modification.
   *
   * @param row The CSV row represented as a List of String values.
   * @return The same row passed as input, fulfilling the CreatorFromRow contract.
   */
  @Override
  public List<String> create(List<String> row) {
    // Simply return the input row, as this creator keeps the object as a list of strings
    return row;
  }

  @Override
  public List<String> toListOfString(List<String> object) {
    return object;
  }
}
