package no.leo.studentmanager.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import no.leo.studentmanager.exception.DatabaseException;
import no.leo.studentmanager.model.Course;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CourseServiceTest {
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
  void testCreateCourse() throws DatabaseException {
    Course course = new Course(0, "Test Course");
    Course created = courseService.create(course);

    assertNotNull(created);
    assertTrue(created.getId() > 0);
    assertEquals(course.getName(), created.getName());
  }

  @Test
  void testGetByName() throws DatabaseException {
    Course course = new Course(0, "Test Course");
    courseService.create(course);

    Course found = courseService.getByName("Test Course");
    assertNotNull(found);
    assertEquals(course.getName(), found.getName());
  }
}