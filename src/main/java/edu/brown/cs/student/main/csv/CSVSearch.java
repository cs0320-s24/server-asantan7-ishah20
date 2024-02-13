package edu.brown.cs.student.main.csv;

import edu.brown.cs.student.main.csv.CreatorFromRowExamples.CreatorFromRow;
import edu.brown.cs.student.main.csv.Exceptions.ColumnIndexOutOfBoundsException;
import edu.brown.cs.student.main.csv.Exceptions.FactoryFailureException;
import edu.brown.cs.student.main.csv.Exceptions.InvalidHeaderNameException;
import java.io.IOException;
import java.util.List;

/**
 * Class for searching through CSV data (that has been parsed into objects of type T) based on a
 * specified value.
 *
 * @param <T> the type of objects that the CSV data is parsed into, which contains data from each
 *     CSV row
 */
public class CSVSearch<T> {
  private CSVParser<T> csvParser;
  private CreatorFromRow creatorFromRow;

  /**
   * Constructs a CSVSearch object.
   *
   * @param csvParser the CSVParser instance used to parse CSV data into objects of type T
   * @param creatorFromRow ADD
   */
  public CSVSearch(CSVParser<T> csvParser, CreatorFromRow<T> creatorFromRow) {
    this.csvParser = csvParser;
    this.creatorFromRow = creatorFromRow;
  }

  /**
   * Searches through the CSV data for rows that match the specified value in the given column. Rows
   * with the value of interest are printed using System.out.println.
   *
   * <p>If the columnIdentifier is null, the search is performed across all columns. If the
   * columnIdentifier is an integer, it is treated as a column index (0-based). If the
   * columnIdentifier is a string, it is treated as a column name.
   *
   * @param value the value to search for in the CSV data
   * @param columnIdentifier the identifier for the column to search in (can be null, Integer, or
   *     String)
   * @throws IOException if an I/O error occurs reading from the CSV source
   * @throws FactoryFailureException if the parser fails to create an object from a row
   */
  public void search(String value, Object columnIdentifier)
      throws IOException, FactoryFailureException, ColumnIndexOutOfBoundsException,
          InvalidHeaderNameException {

    boolean hasFound = false; // Used to track if any match is found
    T row;

    while ((row = csvParser.parseNextRow()) != null) {
      List<String> rowList = this.creatorFromRow.toListOfString(row);
      if (isMatch(rowList, value, columnIdentifier)) {
        System.out.println(rowList);
        hasFound = true; // Set the flag to true if a match is found
      }
    }

    if (!hasFound) {
      System.out.println("No results found for the search criteria!");
    }
  }

  /**
   * Determines if the given row matches the search criteria.
   *
   * @param row the row to check for a match
   * @param value the value to search for
   * @param columnIdentifier the identifier of the column to search (can be null, Integer, or
   *     String)
   * @return true if the row matches the search criteria, false otherwise
   * @throws ColumnIndexOutOfBoundsException if the index is out of bounds
   * @throws InvalidHeaderNameException if header name is invalid
   */
  private boolean isMatch(List<String> row, String value, Object columnIdentifier)
      throws ColumnIndexOutOfBoundsException, InvalidHeaderNameException {
    // Case #1: Column identifier is left unspecified:
    if (columnIdentifier == null) {
      return row.stream().anyMatch(cell -> cell.contains(value));
    }

    // Case #2: Column identifier is an integer:
    if (columnIdentifier instanceof Integer) {
      int columnIndex = (Integer) columnIdentifier;
      if (columnIndex < 0 || columnIndex >= row.size()) {
        throw new ColumnIndexOutOfBoundsException(
            "Column index " + columnIndex + " is out of bounds.");
      }
      return row.get(columnIndex).contains(value);
    }

    // Case #3: Column identifier is the column's name:
    if (columnIdentifier instanceof String && this.csvParser.getHeaderColumns() != null) {
      String columnName = (String) columnIdentifier;
      int columnIndex = this.csvParser.getHeaderColumns().indexOf(columnName);
      if (columnIndex == -1) {
        throw new InvalidHeaderNameException("Column name '" + columnName + "' is invalid.");
      }
      if (columnIndex >= row.size()) {
        throw new ColumnIndexOutOfBoundsException(
            "Column index for '" + columnName + "' is out of bounds.");
      }
      return row.get(columnIndex).contains(value);
    }

    return false;
  }
}
