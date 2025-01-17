package no.leo.studentmanager.exception;

/**
 * This exception is thrown when a specific record is not found in the database.
 * It extends {@code DatabaseException}, providing a specific exception type
 * for handling scenarios where a requested record cannot be located.
 */
public class RecordNotFoundException extends DatabaseException {
  /**
   * Constructs a new RecordNotFoundException with the specified detail message.
   * This exception is specifically used to signal that a requested record was not found in the database.
   *
   * @param message the detail message explaining the reason for the exception
   */
  public RecordNotFoundException(String message) {
    super(message);
  }
}