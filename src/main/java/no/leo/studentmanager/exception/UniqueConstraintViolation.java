package no.leo.studentmanager.exception;

/**
 * This exception is thrown when a unique constraint violation occurs during a database operation.
 * It extends {@code DatabaseException}, providing a specific exception type for handling
 * scenarios where a record's field value violates the unique constraint defined on the database.
 * <p>
 * Unique constraint violations typically occur when attempting to create or update a record,
 * and the value of one of its fields conflicts with an existing entry in the database.
 */
public class UniqueConstraintViolation extends DatabaseException {
  /**
   * Constructs a new UniqueConstraintViolation exception with the specified detail message.
   * This exception is thrown when a unique constraint violation occurs during a database operation.
   * It is used to signal that a field value violates the unique constraint defined on the database.
   *
   * @param message the detail message explaining the reason for the unique constraint violation
   */
  public UniqueConstraintViolation(String message) {
    super(message);
  }
}
