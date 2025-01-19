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

  public void enrollStudent(int studentId, int courseId) throws DatabaseException {
    CourseEnrollment enrollment = new CourseEnrollment(0, studentId, courseId, 0);

    // no unique_together in this table, or rather not implemented yet, so we need to check if the enrollment already exists
    if (this.getByStudent(studentId).stream().anyMatch(e -> e.getCourseId() == courseId)) {
      throw new DatabaseException("Student is already enrolled in this course");
    }
    this.create(enrollment);
  }

  public void setGrade(int studentId, int courseId, int grade) throws DatabaseException {
    List<CourseEnrollment> enrollments = this.getByStudent(studentId);
    CourseEnrollment enrollment = enrollments.stream()
        .filter(e -> e.getCourseId() == courseId)
        .findFirst()
        .orElse(null);

    if (enrollment == null) {
      throw new DatabaseException("Student is not enrolled in this course, enroll first using 'enroll-student'");
    }

    enrollment.setGrade(grade);
    this.update(enrollment);
  }
}