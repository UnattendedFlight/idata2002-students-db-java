package no.leo.studentmanager.exception;

/**
 * Represents a general exception that occurs during database operations.
 * This exception serves as a base class for more specific database-related exceptions.
 */
public class DatabaseException extends Exception {
  /**
   * Constructs a new DatabaseException with the specified detail message.
   * This exception is a general-purpose exception for database operations,
   * typically used as a base class for more specific database-related exceptions.
   *
   * @param message the detail message explaining the reason for the exception
   */
  public DatabaseException(String message) {
    super(message);
  }
}