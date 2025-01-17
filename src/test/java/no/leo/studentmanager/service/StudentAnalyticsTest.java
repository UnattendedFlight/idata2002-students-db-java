package no.leo.studentmanager.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import no.leo.studentmanager.exception.DatabaseException;
import no.leo.studentmanager.model.Course;
import no.leo.studentmanager.model.CourseEnrollment;
import no.leo.studentmanager.model.Student;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class StudentAnalyticsTest {
  private StudentAnalytics analytics;
  private StudentService studentService;
  private CourseService courseService;
  private CourseEnrollmentService enrollmentService;
  private static final String TEST_DB_PATH = "test_db";

  @BeforeEach
  void setUp() throws Exception {
    Files.createDirectories(Paths.get(TEST_DB_PATH));

    studentService = new StudentService(TEST_DB_PATH);
    courseService = new CourseService(TEST_DB_PATH);
    enrollmentService = new CourseEnrollmentService(TEST_DB_PATH);
    analytics = new StudentAnalytics(studentService, courseService, enrollmentService);

    // Create test data
    setupTestData();
  }

  private void setupTestData() throws DatabaseException {
    // Create students
    Student student1 =
        studentService.create(new Student(0, "John Doe", "john@test.com", "12345678"));
    Student student2 =
        studentService.create(new Student(0, "Jane Smith", "jane@test.com", "87654321"));

    // Create courses
    Course course1 = courseService.create(new Course(0, "Mathematics"));
    Course course2 = courseService.create(new Course(0, "Physics"));

    // Create enrollments
    enrollmentService.create(new CourseEnrollment(0, student1.getId(), course1.getId(), 4));
    enrollmentService.create(new CourseEnrollment(0, student1.getId(), course2.getId(), 5));
    enrollmentService.create(new CourseEnrollment(0, student2.getId(), course1.getId(), 3));
  }

  @AfterEach
  void tearDown() throws Exception {
    deleteDirectory(new File(TEST_DB_PATH));
  }

  private void deleteDirectory(File directory) {
    File[] files = directory.listFiles();
    if (files != null) {
      for (File file : files) {
        if (file.isDirectory()) {
          deleteDirectory(file);
        } else {
          file.delete();
        }
      }
    }
    directory.delete();
  }

  @Test
  void testGetStudentAverageGrade() throws DatabaseException {
    Student student = studentService.getByEmail("john@test.com");
    Optional<Map.Entry<Student, Double>> result = analytics.getStudentAverageGrade(student.getId());

    assertTrue(result.isPresent());
    assertEquals(4.5, result.get().getValue(), 0.01);
  }

  @Test
  void testGetCourseEnrollmentCount() throws DatabaseException {
    Optional<Map.Entry<Course, Integer>> result = analytics.getCourseEnrollmentCount("Mathematics");

    assertTrue(result.isPresent());
    assertEquals(2, result.get().getValue());
  }

  @Test
  void testGetCourseWithMostStudents() throws DatabaseException {
    Optional<Map.Entry<Course, Integer>> result = analytics.getCourseWithMostStudents();

    assertTrue(result.isPresent());
    assertEquals("Mathematics", result.get().getKey().getName());
    assertEquals(2, result.get().getValue());
  }

  @Test
  void testGetStudentsInCourse() throws DatabaseException {
    Course course = courseService.getByName("Mathematics");
    Optional<Map.Entry<Course, List<Student>>> result =
        analytics.getStudentsInCourse(course.getId());

    assertTrue(result.isPresent());
    assertEquals(2, result.get().getValue().size());
    assertTrue(result.get().getValue().stream()
        .anyMatch(s -> s.getEmail().equals("john@test.com")));
    assertTrue(result.get().getValue().stream()
        .anyMatch(s -> s.getEmail().equals("jane@test.com")));
  }
}