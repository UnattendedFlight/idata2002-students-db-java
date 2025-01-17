package no.leo.studentmanager.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Objects;

/**
 * Represents a course enrollment entity, which stores the relationship
 * between a student and a course along with the grade earned.
 * <p>
 * This class is used to model the linkage between students and courses
 * in the student management system. It includes fields for a unique identifier,
 * the student ID, the course ID, and the grade awarded to the student for the course.
 */
public class CourseEnrollment {
  @JsonProperty("id")
  private int id;
  @JsonProperty("student_id")
  private int studentId;
  @JsonProperty("course_id")
  private int courseId;
  @JsonProperty("grade")
  private int grade;

  // Regular constructor
  public CourseEnrollment(int id, int studentId, int courseId, int grade) {
    this.id = id;
    this.studentId = studentId;
    this.courseId = courseId;
    this.grade = grade;
  }

  /**
   * Creates a new instance of the CourseEnrollment class using the specified parameters.
   * This method is annotated with @JsonCreator to facilitate JSON deserialization
   * using the Jackson library.
   *
   * @param id        the unique identifier for the course enrollment
   * @param studentId the unique identifier for the student
   * @param courseId  the unique identifier for the course
   * @param grade     the grade awarded to the student for the course
   * @return a new instance of the CourseEnrollment class containing the given details
   */
  @JsonCreator
  public static CourseEnrollment create(
      @JsonProperty("id") int id,
      @JsonProperty("student_id") int studentId,
      @JsonProperty("course_id") int courseId,
      @JsonProperty("grade") int grade) {
    return new CourseEnrollment(id, studentId, courseId, grade);
  }

  /**
   * Retrieves the ID of the course enrollment.
   *
   * @return the unique identifier associated with the course enrollment
   */
  // Getters and setters
  public int getId() {
    return id;
  }

  /**
   * Sets the unique identifier for the course enrollment.
   *
   * @param id the unique identifier to be assigned to the course enrollment
   */
  public void setId(int id) {
    this.id = id;
  }

  /**
   * Retrieves the student ID associated with the course enrollment.
   *
   * @return the unique identifier of the student enrolled in the course
   */
  @JsonProperty("student_id")
  public int getStudentId() {
    return studentId;
  }

  /**
   * Sets the student ID for the course enrollment.
   *
   * @param studentId the unique identifier for the student
   */
  @JsonProperty("student_id")
  public void setStudentId(int studentId) {
    this.studentId = studentId;
  }

  /**
   * Retrieves the unique identifier of the course associated with this enrollment.
   *
   * @return the unique identifier of the course
   */
  @JsonProperty("course_id")
  public int getCourseId() {
    return courseId;
  }

  /**
   * Sets the ID of the course associated with the course enrollment.
   *
   * @param courseId the unique identifier of the course to be assigned
   */
  @JsonProperty("course_id")
  public void setCourseId(int courseId) {
    this.courseId = courseId;
  }

  /**
   * Retrieves the grade associated with the course enrollment.
   *
   * @return the grade awarded to the student for the course
   */
  public int getGrade() {
    return grade;
  }

  /**
   * Sets the grade for the course enrollment.
   *
   * @param grade the grade to be assigned to the course enrollment
   */
  public void setGrade(int grade) {
    this.grade = grade;
  }

  /**
   * Returns a string representation of the CourseEnrollment object.
   * The string includes the values of the id, studentId, courseId, and grade attributes.
   *
   * @return a string representation of the object in the format
   * "CourseEnrollment{id=value, studentId=value, courseId=value, grade=value}"
   */
  @Override
  public String toString() {
    return "CourseEnrollment{" +
        "id=" + id +
        ", studentId=" + studentId +
        ", courseId=" + courseId +
        ", grade=" + grade +
        '}';
  }

  /**
   * Compares this CourseEnrollment object with another object to determine equality.
   * The comparison is based on the `id`, `studentId`, `courseId`, and `grade` fields.
   *
   * @param o the object to compare with this CourseEnrollment instance
   * @return true if the specified object is of type CourseEnrollment and has the same
   * `id`, `studentId`, `courseId`, and `grade` values; false otherwise
   */
  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    CourseEnrollment that = (CourseEnrollment) o;
    return id == that.id &&
        studentId == that.studentId &&
        courseId == that.courseId &&
        grade == that.grade;
  }

  /**
   * Computes the hash code for the current instance of the object.
   * The hash code is calculated based on the `id`, `studentId`, `courseId`, and `grade` fields.
   *
   * @return an integer hash code value for the object
   */
  @Override
  public int hashCode() {
    return Objects.hash(id, studentId, courseId, grade);
  }
}