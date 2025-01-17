package no.leo.studentmanager.service;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import no.leo.studentmanager.exception.DatabaseException;
import no.leo.studentmanager.model.Student;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class StudentServiceTest {
  private StudentService studentService;
  private static final String TEST_DB_PATH = "test_db";

  @BeforeEach
  void setUp() throws Exception {
    // Create test database directory
    Files.createDirectories(Paths.get(TEST_DB_PATH));

    // Copy table_definitions.json to test directory if needed
    Path sourceFile = Paths.get("table_definitions.json");
    Path targetFile = Paths.get(TEST_DB_PATH, "table_definitions.json");
    if (!Files.exists(targetFile)) {
      Files.copy(sourceFile, targetFile);
    }

    studentService = new StudentService(TEST_DB_PATH);
  }

  @AfterEach
  void tearDown() throws Exception {
    // Clean up test database
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
  void testCreateStudent() throws DatabaseException {
    Student student = new Student(0, "Test Student", "test@test.com", "12345678");
    Student created = studentService.create(student);

    assertNotNull(created);
    assertTrue(created.getId() > 0);
    assertEquals(student.getName(), created.getName());
    assertEquals(student.getEmail(), created.getEmail());
    assertEquals(student.getPhone(), created.getPhone());
  }

  @Test
  void testGetByEmail() throws DatabaseException {
    Student student = new Student(0, "Test Student", "test@test.com", "12345678");
    studentService.create(student);

    Student found = studentService.getByEmail("test@test.com");
    assertNotNull(found);
    assertEquals(student.getName(), found.getName());
  }

  @Test
  void testDuplicateEmail() {
    Student student1 = new Student(0, "Test Student 1", "test@test.com", "12345678");
    Student student2 = new Student(0, "Test Student 2", "test@test.com", "87654321");

    assertDoesNotThrow(() -> studentService.create(student1));
    assertThrows(DatabaseException.class, () -> studentService.create(student2));
  }
}