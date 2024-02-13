package edu.brown.cs.student.main.csv.CreatorFromRowExamples;

import edu.brown.cs.student.main.csv.Exceptions.FactoryFailureException;
import java.util.List;

/**
 * This interface defines a method that allows your CSV parser to convert each row into an object of
 * some arbitrary passed type.
 *
 * <p>Your parser class constructor should take a second parameter of this generic interface type.
 */
public interface CreatorFromRow<T> {
  T create(List<String> row) throws FactoryFailureException;

  /**
   * Converts an object of type T back into a List of Strings, representing a CSV row.
   *
   * @param object The object of type T to be converted.
   * @return A List of Strings representing the CSV row.
   */
  List<String> toListOfString(T object);
}
