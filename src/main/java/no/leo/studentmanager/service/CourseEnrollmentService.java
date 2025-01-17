package no.leo.studentmanager.service;

import java.util.List;
import no.leo.studentmanager.exception.DatabaseException;
import no.leo.studentmanager.model.CourseEnrollment;

/**
 * Service class for managing course enrollments. Provides functionality
 * to handle enrollment-related operations, such as retrieving enrollments
 * by student or course.
 */
public class CourseEnrollmentService extends BaseService<CourseEnrollment> {

  public CourseEnrollmentService(String dbPath) {
    super(CourseEnrollment.class, dbPath);
  }

  /**
   * Retrieves the name of the database table associated with course enrollments.
   *
   * @return The table name for course enrollments.
   */
  @Override
  protected String getTableName() {
    return "course_enrollments";
  }

  /**
   * Get all enrollments for a specific student
   *
   * @param studentId The student's ID
   * @return List of course enrollments for the student
   * @throws DatabaseException if there's an error accessing the database
   */
  public List<CourseEnrollment> getByStudent(int studentId) throws DatabaseException {
    return this.getByField("student_id", studentId);
  }

  /**
   * Get all enrollments for a specific course
   *
   * @param courseId The course ID
   * @return List of course enrollments for the course
   * @throws DatabaseException if there's an error accessing the database
   */
  public List<CourseEnrollment> getByCourse(int courseId) throws DatabaseException {
    return this.getByField("course_id", courseId);
  }
}