package edu.brown.cs.student.main.csv.Exceptions;

/** Catches error if the column header name given does not exist */
public class InvalidHeaderNameException extends Exception {
  public InvalidHeaderNameException(String message) {
    super(message);
  }
}
