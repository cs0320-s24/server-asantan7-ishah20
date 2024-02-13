package edu.brown.cs.student.main.csv.Exceptions;

/** Catches error if a column index is out of bounds */
public class ColumnIndexOutOfBoundsException extends Exception {
  public ColumnIndexOutOfBoundsException(String message) {
    super(message);
  }
}
