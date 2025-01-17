package no.leo.studentmanager.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import no.leo.studentmanager.exception.DatabaseException;
import no.leo.studentmanager.model.Course;
import no.leo.studentmanager.model.CourseEnrollment;
import no.leo.studentmanager.model.Student;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CourseEnrollmentServiceTest {
  private CourseEnrollmentService enrollmentService;
  private StudentService studentService;
  private CourseService courseService;
  private static final String TEST_DB_PATH = "test_db";

  @BeforeEach
  void setUp() throws Exception {
    Files.createDirectories(Paths.get(TEST_DB_PATH));
    Path sourceFile = Paths.get("table_definitions.json");
    Path targetFile = Paths.get(TEST_DB_PATH, "table_definitions.json");
    if (!Files.exists(targetFile)) {
      Files.copy(sourceFile, targetFile);
    }

    enrollmentService = new CourseEnrollmentService(TEST_DB_PATH);
    studentService = new StudentService(TEST_DB_PATH);
    courseService = new CourseService(TEST_DB_PATH);
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
  void testCreateEnrollment() throws DatabaseException {
    // Create test student and course first
    Student
        student = studentService.create(new Student(0, "Test Student", "test@test.com", "12345678"));
    Course course = courseService.create(new Course(0, "Test Course"));

    CourseEnrollment enrollment = new CourseEnrollment(0, student.getId(), course.getId(), 4);
    CourseEnrollment created = enrollmentService.create(enrollment);

    assertNotNull(created);
    assertTrue(created.getId() > 0);
    assertEquals(student.getId(), created.getStudentId());
    assertEquals(course.getId(), created.getCourseId());
    assertEquals(4, created.getGrade());
  }

  @Test
  void testGetByStudent() throws DatabaseException {
    // Create test data
    Student student = studentService.create(new Student(0, "Test Student", "test@test.com", "12345678"));
    Course course1 = courseService.create(new Course(0, "Course 1"));
    Course course2 = courseService.create(new Course(0, "Course 2"));

    enrollmentService.create(new CourseEnrollment(0, student.getId(), course1.getId(), 4));
    enrollmentService.create(new CourseEnrollment(0, student.getId(), course2.getId(), 5));

    List<CourseEnrollment> enrollments = enrollmentService.getByStudent(student.getId());

    assertEquals(2, enrollments.size());
    assertTrue(enrollments.stream().allMatch(e -> e.getStudentId() == student.getId()));
  }

  @Test
  void testGetByCourse() throws DatabaseException {
    // Create test data
    Student student1 = studentService.create(new Student(0, "Student 1", "test1@test.com", "12345678"));
    Student student2 = studentService.create(new Student(0, "Student 2", "test2@test.com", "87654321"));
    Course course = courseService.create(new Course(0, "Test Course"));

    enrollmentService.create(new CourseEnrollment(0, student1.getId(), course.getId(), 4));
    enrollmentService.create(new CourseEnrollment(0, student2.getId(), course.getId(), 5));

    List<CourseEnrollment> enrollments = enrollmentService.getByCourse(course.getId());

    assertEquals(2, enrollments.size());
    assertTrue(enrollments.stream().allMatch(e -> e.getCourseId() == course.getId()));
  }
}