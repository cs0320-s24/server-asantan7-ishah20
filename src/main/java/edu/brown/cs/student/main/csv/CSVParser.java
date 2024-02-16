package edu.brown.cs.student.main.csv;

import edu.brown.cs.student.main.csv.CreatorFromRowExamples.CreatorFromRow;
import edu.brown.cs.student.main.csv.Exceptions.FactoryFailureException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Parses CSV data from a given object that extends the Reader abstract class and creates objects of
 * type T for each row using a specified CreatorFromRow.
 *
 * @param <T> the type of objects to be created from each CSV row
 */
public class CSVParser<T> {
  private BufferedReader reader;
  private CreatorFromRow<T> creator;
  private boolean hasHeader;
  private List<String> headerColumns = null;
  private final Pattern regexSplitCSVRow = Pattern.compile(",(?=([^\"]*\"[^\"]*\")*(?![^\"]*\"))");

  /**
   * Constructor for the CSVParser class.
   *
   * @param reader the source from which CSV data is read
   * @param creator the CreatorFromRow instance used to create objects of type T from CSV rows
   * @param hasHeader true if the first row of the CSV data is a header row, false otherwise
   * @throws IllegalArgumentException if either reader or creator is null
   */
  public CSVParser(Reader reader, CreatorFromRow<T> creator, boolean hasHeader) throws IOException {
    if (reader == null || creator == null) {
      throw new IllegalArgumentException("Reader and Creator cannot be null.");
    }
    this.reader = new BufferedReader(reader);
    this.creator = creator;
    this.hasHeader = hasHeader;

    // If the first row of the CSV data is a header, store header in the headerColumn fields
    if (hasHeader) {
      String headerLine = this.reader.readLine();
      if (headerLine != null) {
        this.headerColumns = List.of(regexSplitCSVRow.split(headerLine));
      }
    }
  }

  /**
   * Getter method for the headerColumn field
   *
   * @return a List of strings containing the names of the header columns, or null if the header is
   *     not available or not defined.
   */
  public List<String> getHeaderColumns() {
    return this.headerColumns;
  }

  /**
   * Parses a row from the CSV data source, creating an object of type T.
   *
   * @return an object of type T created from the next CSV row, or null if the end of the source is
   *     reached
   * @throws IOException if an I/O error occurs reading from the source
   * @throws FactoryFailureException if the creator fails to create an object from the row
   */
  public T parseNextRow() throws IOException, FactoryFailureException {
    String line = this.reader.readLine();

    if (line != null) {
      String[] rowValues = regexSplitCSVRow.split(line);
      try {
        return this.creator.create(List.of(rowValues));
      } catch (Exception e) {
        throw new FactoryFailureException("Failed to create object from row", List.of(rowValues));
      }
    }
    return null;
  }
}
