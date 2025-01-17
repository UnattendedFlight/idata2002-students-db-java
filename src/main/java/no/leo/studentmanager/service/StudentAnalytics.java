package no.leo.studentmanager.service;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import no.leo.studentmanager.exception.DatabaseException;
import no.leo.studentmanager.model.Course;
import no.leo.studentmanager.model.CourseEnrollment;
import no.leo.studentmanager.model.Student;

/**
 * The StudentAnalytics class provides methods for analyzing and retrieving
 * information about students, courses, and their enrollments. It interacts
 * with services such as StudentService, CourseService, and CourseEnrollmentService
 * to fetch and process data.
 * <p>
 * This class supports operations such as calculating a student's average grade,
 * determining course enrollment counts, identifying the course with the most students,
 * and listing students enrolled in a specified course. Results are often wrapped
 * in Optional objects to handle scenarios where data is unavailable or invalid.
 */
public class StudentAnalytics {
  private final StudentService studentService;
  private final CourseService courseService;
  private final CourseEnrollmentService enrollmentService;

  /**
   * Default constructor for the StudentAnalytics class.
   * This constructor initializes the necessary service dependencies
   * required for analytics operations on student, course, and course enrollment data.
   * <p>
   * The following services are initialized with a default database connection:
   * - StudentService for accessing and managing student-related data.
   * - CourseService for accessing and managing course-related data.
   * - CourseEnrollmentService for managing course enrollment data.
   */
  public StudentAnalytics() {
    this.studentService = new StudentService("db");
    this.courseService = new CourseService("db");
    this.enrollmentService = new CourseEnrollmentService("db");
  }

  /**
   * Constructor for the StudentAnalytics class.
   * This constructor initializes the service dependencies required for processing
   * and analyzing student, course, and course enrollment data using the specified database path.
   *
   * @param db_path The file path to the database used for initializing the services.
   */
  public StudentAnalytics(String db_path) {
    this.studentService = new StudentService(db_path);
    this.courseService = new CourseService(db_path);
    this.enrollmentService = new CourseEnrollmentService(db_path);
  }

  /**
   * Constructor for the StudentAnalytics class.
   * This constructor initializes the service dependencies required for
   * performing analytics operations on student, course, and course enrollment data.
   *
   * @param studentService    the service responsible for managing and retrieving student-related data
   * @param courseService     the service responsible for managing and retrieving course-related data
   * @param enrollmentService the service responsible for managing and retrieving course enrollment data
   */
  public StudentAnalytics(StudentService studentService, CourseService courseService,
                          CourseEnrollmentService enrollmentService) {
    this.studentService = studentService;
    this.courseService = courseService;
    this.enrollmentService = enrollmentService;
  }

  /**
   * Calculates the average grade of a student based on their course enrollments.
   * The method retrieves student data using a name or ID and computes the average
   * grade across all their enrolled courses.
   *
   * @param studentNameOrId the name (String) or ID (Integer) of the student for whom the average grade
   *                        should be calculated. Must be either a String or an Integer, otherwise
   *                        an IllegalArgumentException is thrown.
   * @return an Optional containing a Map.Entry where the key is the Student object and the value is
   * the average grade (Double), rounded to two decimal places. If no student is found or if
   * the student has no enrollments, it returns an Optional with a grade of 0.0 or empty if
   * invalid data is provided.
   * @throws DatabaseException if a database-related error occurs, or if multiple students with the
   *                           same name are found.
   */
  public Optional<Map.Entry<Student, Double>> getStudentAverageGrade(Object studentNameOrId)
      throws DatabaseException {
    try {
      Student student;

      if (studentNameOrId == null) {
        return Optional.empty();
      }

      if (studentNameOrId instanceof String) {
        List<Student> students = this.studentService.getByName((String) studentNameOrId);
        if (students == null || students.isEmpty()) {
          return Optional.empty();
        }
        if (students.size() > 1) {
          throw new DatabaseException("Multiple students with same name");
        }
        student = students.getFirst();
      } else if (studentNameOrId instanceof Integer) {
        student = this.studentService.getById((Integer) studentNameOrId);
        if (student == null) {
          return Optional.empty();
        }
      } else {
        throw new IllegalArgumentException("studentNameOrId must be either String or Integer");
      }

      List<CourseEnrollment> enrollments = this.enrollmentService.getByStudent(student.getId());

      if (enrollments == null || enrollments.isEmpty()) {
        return Optional.of(Map.entry(student, 0.0));
      }

      double average = enrollments.stream()
          .mapToInt(CourseEnrollment::getGrade)
          .average()
          .orElse(0.0);

      // Round to 2 decimal places to match Python implementation
      average = Math.round(average * 100.0) / 100.0;

      return Optional.of(Map.entry(student, average));

    } catch (DatabaseException e) {
      throw e;
    } catch (Exception e) {
      throw new DatabaseException("Error calculating student average grade: " + e.getMessage());
    }
  }

  /**
   * Retrieves the enrollment count for a specific course based on its name or ID.
   * The method fetches the course by either its name (String) or ID (Integer),
   * and calculates the number of students enrolled in that course.
   *
   * @param courseNameOrId the name (String) or ID (Integer) of the course. Must be either a String
   *                       or an Integer; otherwise, an IllegalArgumentException is thrown.
   * @return an Optional containing a Map.Entry where the key is the Course object and the value is
   * the number of students enrolled in the course. Returns an empty Optional if the course
   * is not found or if input is null.
   * @throws DatabaseException if an issue occurs while accessing the database, such as a failure
   *                           when fetching the course or its enrollments.
   */
  public Optional<Map.Entry<Course, Integer>> getCourseEnrollmentCount(Object courseNameOrId)
      throws DatabaseException {
    try {
      if (courseNameOrId == null) {
        return Optional.empty();
      }

      Course course;
      if (courseNameOrId instanceof String) {
        course = this.courseService.getByName((String) courseNameOrId);
      } else if (courseNameOrId instanceof Integer) {
        course = this.courseService.getById((Integer) courseNameOrId);
      } else {
        throw new IllegalArgumentException("courseNameOrId must be either String or Integer");
      }

      if (course == null) {
        return Optional.empty();
      }

      List<CourseEnrollment> enrollments = this.enrollmentService.getByCourse(course.getId());
      return Optional.of(Map.entry(course, enrollments != null ? enrollments.size() : 0));

    } catch (DatabaseException e) {
      return Optional.empty();
    }
  }

  /**
   * Retrieves the course with the most students enrolled along with the count of enrollments.
   * This method accesses all available courses, evaluates their enrollment counts,
   * and identifies the course with the highest number of students.
   *
   * @return an Optional containing a Map.Entry where the key is the Course with the most students
   * and the value is the count of students. If no courses are available or all courses
   * have zero enrollments, an empty Optional is returned.
   * @throws DatabaseException if an error occurs while accessing the database or fetching data.
   */
  public Optional<Map.Entry<Course, Integer>> getCourseWithMostStudents() throws DatabaseException {
    try {
      List<Course> courses = this.courseService.getAll();
      if (courses == null || courses.isEmpty()) {
        return Optional.empty();
      }

      Course mostStudents = null;
      int mostStudentsCount = 0;

      for (Course course : courses) {
        List<CourseEnrollment> enrollments = this.enrollmentService.getByCourse(course.getId());
        int enrollmentCount = enrollments != null ? enrollments.size() : 0;
        if (enrollmentCount > mostStudentsCount) {
          mostStudents = course;
          mostStudentsCount = enrollmentCount;
        }
      }

      if (mostStudents == null) {
        return Optional.empty();
      }

      return Optional.of(Map.entry(mostStudents, mostStudentsCount));

    } catch (DatabaseException e) {
      throw e;
    } catch (Exception e) {
      throw new DatabaseException("Error finding course with most students: " + e.getMessage());
    }
  }

  /**
   * Retrieves the students enrolled in a specified course.
   * The course can be identified by its name (String) or ID (Integer).
   * If a valid course is found, its student enrollments are retrieved,
   * and a list of students sorted by name is returned along with the course details.
   *
   * @param courseNameOrId the name (String) or ID (Integer) of the course. Must be either a String
   *                       or an Integer; otherwise, an IllegalArgumentException is thrown.
   * @return an Optional containing a Map.Entry where the key is the Course object and the value
   * is a list of Student objects enrolled in that course. Returns an empty Optional if
   * the course is not found or if no students are enrolled.
   * @throws DatabaseException if any database-related error occurs, such as failure while
   *                           fetching the course, its enrollments, or the associated students.
   */
  public Optional<Map.Entry<Course, List<Student>>> getStudentsInCourse(Object courseNameOrId)
      throws DatabaseException {
    try {
      if (courseNameOrId == null) {
        return Optional.empty();
      }

      Course course;
      if (courseNameOrId instanceof Integer) {
        course = this.courseService.getById((Integer) courseNameOrId);
      } else if (courseNameOrId instanceof String) {
        course = this.courseService.getByName((String) courseNameOrId);
      } else {
        throw new IllegalArgumentException("courseNameOrId must be either String or Integer");
      }

      if (course == null) {
        return Optional.empty();
      }

      List<CourseEnrollment> enrollments = this.enrollmentService.getByCourse(course.getId());
      if (enrollments == null) {
        enrollments = Collections.emptyList();
      }

      List<Student> students = enrollments.stream()
          .map(enrollment -> {
            try {
              return this.studentService.getById(enrollment.getStudentId());
            } catch (DatabaseException e) {
              return null;
            }
          })
          .filter(Objects::nonNull)
          .sorted(Comparator.comparing(Student::getName))
          .toList();

      return Optional.of(Map.entry(course, students));

    } catch (DatabaseException e) {
      throw e;
    } catch (Exception e) {
      throw new DatabaseException("Error getting students in course: " + e.getMessage());
    }
  }
}