package no.leo.studentmanager.service;

import java.util.List;
import no.leo.studentmanager.exception.DatabaseException;
import no.leo.studentmanager.model.Student;

/**
 * Service class to perform operations related to Student entities.
 * Extends the {@code BaseService} class to provide basic database
 * operations and introduces additional methods specific to Student handling.
 */
public class StudentService extends BaseService<Student> {

  public StudentService(String dbPath) {
    super(Student.class, dbPath);
  }

  /**
   * Retrieves the name of the database table associated with the Student entity.
   *
   * @return the name of the database table, which is "students".
   */
  @Override
  protected String getTableName() {
    return "students";
  }

  /**
   * Retrieves a Student entity by its email address.
   * If no student is found with the provided email, the method returns {@code null}.
   * If multiple students are found with the same email, a {@code DatabaseException} is thrown.
   *
   * @param email the email address of the student to be retrieved
   * @return the {@code Student} object associated with the given email,
   * or {@code null} if no student is found
   * @throws DatabaseException if multiple students are found with the same email
   */
  public Student getByEmail(String email) throws DatabaseException {
    List<Student> students = this.getByField("email", email);
    if (students.isEmpty()) {
      return null;
    }
    if (students.size() > 1) {
      throw new DatabaseException("Multiple students with same email");
    }
    return students.getFirst();
  }

  /**
   * Retrieves a single student record based on the provided phone number.
   * If no student is found with the given phone number, the method returns {@code null}.
   * If multiple students are found with the same phone number, a {@code DatabaseException} is thrown.
   *
   * @param phone the phone number to search for
   * @return the {@code Student} object matching the specified phone number, or {@code null} if no match is found
   * @throws DatabaseException if multiple students are found with the same phone number
   */
  public Student getByPhone(String phone) throws DatabaseException {
    List<Student> students = this.getByField("phone", phone);
    if (students.isEmpty()) {
      return null;
    }
    if (students.size() > 1) {
      throw new DatabaseException("Multiple students found with same phone number");
    }
    return students.getFirst();
  }

  /**
   * Retrieves a list of students matching the specified name.
   *
   * @param name the name of the students to retrieve
   * @return a list of {@code Student} objects that match the given name
   * @throws DatabaseException if an error occurs during database interaction
   */
  public List<Student> getByName(String name) throws DatabaseException {
    return this.getByField("name", name);
  }
}