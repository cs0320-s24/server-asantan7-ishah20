package edu.brown.cs.student.main.csv;

import edu.brown.cs.student.main.csv.CreatorFromRowExamples.CreatorFromRow;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.util.List;

/**
 * A user-facing application class, which acts as the entry point for the CSV file search. This
 * application prompts the user to enter search queries for CSV files located within a specific
 * directory.
 */
public final class Main {
  // The root path where the CSV files are expected to be located.
  private static final String ROOT_PATH =
      "/Users/annaarantes/Desktop/CS320/csv-annasantannaarantes/data/";

  /**
   * Starts the application and processes user input from the console to perform search operations
   * on CSV files.
   */
  public static void main(String[] args) {
    try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
      while (true) {
        System.out.println("\nSearch a CSV File!");
        System.out.println("Type 'exit' to quit at any time.");

        System.out.print("Enter the CSV filepath (relative to the Data folder): ");
        String csvFileName = reader.readLine();
        if ("exit".equalsIgnoreCase(csvFileName)) break;

        System.out.print("Enter the search value: ");
        String searchValue = reader.readLine();
        if ("exit".equalsIgnoreCase(searchValue)) break;

        System.out.print("Does your CSV have headers? (true/false): ");
        String hasHeadersInput = reader.readLine();
        if ("exit".equalsIgnoreCase(hasHeadersInput)) break;
        boolean hasHeaders = Boolean.parseBoolean(hasHeadersInput);

        System.out.print(
            "Enter column index/name to search in (leave blank to search all columns): ");
        String columnIdInput = reader.readLine();
        if ("exit".equalsIgnoreCase(columnIdInput)) break;

        Object columnIdentifier = null;
        if (!columnIdInput.isEmpty()) {
          if (columnIdInput.matches("\\d+")) {
            columnIdentifier = Integer.parseInt(columnIdInput);
          } else {
            columnIdentifier = columnIdInput;
          }
        }

        String csvFilePath = ROOT_PATH + csvFileName;
        try (FileReader fileReader = new FileReader(csvFilePath)) {
          CreatorFromRow<List<String>> creator =
              new edu.brown.cs.student.main.csv.CreatorFromRowExamples.SimpleCreator();
          CSVParser<List<String>> parser = new CSVParser<>(fileReader, creator, hasHeaders);
          CSVSearch<List<String>> csvSearch = new CSVSearch<>(parser, creator);

          csvSearch.search(searchValue, columnIdentifier);
        } catch (Exception e) {
          System.err.println("An error occurred during CSV search: " + e.getMessage());
          System.exit(1);
        }
      }
    } catch (Exception e) {
      System.err.println("Error reading input: " + e.getMessage());
      System.exit(1);
    }
  }
}
