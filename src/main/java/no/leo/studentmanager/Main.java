package no.leo.studentmanager;

import java.util.*;
import no.leo.studentmanager.exception.DatabaseException;
import no.leo.studentmanager.model.*;
import no.leo.studentmanager.service.*;

public class Main {
  private static String gradeMapping(double grade) {
    if (grade >= 4.5) return "A";
    if (grade >= 3.5) return "B";
    if (grade >= 2.5) return "C";
    if (grade >= 1.5) return "D";
    if (grade >= 0.5) return "E";
    return "F";
  }

  private static List<Student> createStudents(StudentService studentService) {
    List<Student> students = Arrays.asList(
        new Student(0, "Ole Hansen", "olehans@stud.ntnu.no", "91234567"),
        new Student(0, "Ole Hansen", "olehans2@stud.ntnu.no", "91234566"),
        new Student(0, "Ingrid Larsen", "ingridl@stud.ntnu.no", "92345678"),
        new Student(0, "Magnus Andreassen", "magnusa@stud.ntnu.no", "93456789"),
        new Student(0, "Sofia Nilsen", "sofian@stud.ntnu.no", "94567890"),
        new Student(0, "Erik Johansen", "erikj@stud.ntnu.no", "95678901")
    );

    List<Student> createdStudents = new ArrayList<>();
    for (Student student : students) {
      try {
        Student created = studentService.create(student);
        createdStudents.add(created);
        System.out.println("Created student: " + created);
      } catch (DatabaseException e) {
        System.out.println("Error creating student " + student.getName() + ": " + e.getMessage());
      }
    }

    return createdStudents;
  }

  private static List<Course> createCourses(CourseService courseService) {
    List<Course> courses = Arrays.asList(
        new Course(0, "IDATA2002 - Databaser"),
        new Course(0, "IDATA2003 - Programmering 2"),
        new Course(0, "IMAA2024 - Matematikk 2")
    );

    List<Course> createdCourses = new ArrayList<>();
    for (Course course : courses) {
      try {
        Course created = courseService.create(course);
        createdCourses.add(created);
        System.out.println("Created course: " + created);
      } catch (DatabaseException e) {
        System.out.println("Error creating course " + course.getName() + ": " + e.getMessage());
      }
    }

    return createdCourses;
  }

  private static List<CourseEnrollment> createEnrollments(
      CourseEnrollmentService enrollmentService,
      List<Student> students,
      List<Course> courses) {
    List<CourseEnrollment> enrollments = new ArrayList<>();
    Random random = new Random();
    if (students.isEmpty()) {
      System.out.println("No enrollments to create..");
      return new ArrayList<>();
    }
    for (Student student : students) {
      for (Course course : courses) {
        if (random.nextDouble() < 0.3) {
          continue;
        }
        // Generate random grade (A-F converted to 5-0)
        int grade = random.nextInt(6);

        CourseEnrollment enrollment = new CourseEnrollment(
            0,
            student.getId(),
            course.getId(),
            grade
        );

        try {
          CourseEnrollment created = enrollmentService.create(enrollment);
          enrollments.add(created);
          System.out.println("Created enrollment: Student " + student.getName() +
              " in " + course.getName() + " with grade " + grade);
        } catch (DatabaseException e) {
          System.out.println("Error creating enrollment: " + e.getMessage());
        }
      }
    }

    return enrollments;
  }

  public static void main(String[] args) {
    StudentService studentService = new StudentService("db");
    CourseService courseService = new CourseService("db");
    CourseEnrollmentService enrollmentService = new CourseEnrollmentService("db");
    List<Student> students;
    List<Course> courses;
    List<CourseEnrollment> enrollments;
    try {
      System.out.println("Creating students...");
      students = createStudents(studentService);
    } catch (Exception e) {
      System.out.println("Error: " + e.getMessage());
      return;
    }
    try {
      System.out.println("Creating courses...");
      courses = createCourses(courseService);
    } catch (Exception e) {
      System.out.println("Error: " + e.getMessage());
      return;
    }
    try {
      System.out.println("Creating enrollments...");
      enrollments = createEnrollments(enrollmentService, students, courses);
    } catch (Exception e) {
      System.out.println("Error: " + e.getMessage());
      return;
    }
      System.out.println("Database population completed successfully!");
      System.out.println("Created " + students.size() + " students");
      System.out.println("Created " + courses.size() + " courses");
      System.out.println("Created " + enrollments.size() + " enrollments");



    // Run analytics
    StudentAnalytics analytics = new StudentAnalytics();

    try {
      // Get student average grade
      Student student = studentService.getByEmail("olehans2@stud.ntnu.no");
      if (student != null) {
        Optional<Map.Entry<Student, Double>> result = analytics.getStudentAverageGrade(student.getId());
        if (result.isPresent()) {
          Map.Entry<Student, Double> entry = result.get();
          System.out.println("\nStudent: " + entry.getKey().getName() +
              " (ID: " + entry.getKey().getId() + ")");
          System.out.println("Email: " + entry.getKey().getEmail());
          System.out.println("Average grade: " + gradeMapping(entry.getValue()));
        }
      } else {
        System.out.println("\nStudent not found");
      }

      // Course enrollment count
      Optional<Map.Entry<Course, Integer>> courseCount =
          analytics.getCourseEnrollmentCount("IDATA2002 - Databaser");
      if (courseCount.isPresent()) {
        Map.Entry<Course, Integer> entry = courseCount.get();
        System.out.println("\nCourse: " + entry.getKey().getName() +
            " (ID: " + entry.getKey().getId() + ")");
        System.out.println("Number of enrolled students: " + entry.getValue());
      } else {
        System.out.println("\nCourse not found");
      }

      // Course with most students
      Optional<Map.Entry<Course, Integer>> mostStudents = analytics.getCourseWithMostStudents();
      if (mostStudents.isPresent()) {
        Map.Entry<Course, Integer> entry = mostStudents.get();
        System.out.println("\nCourse with most students: " + entry.getKey().getName() +
            " (ID: " + entry.getKey().getId() + ")");
        System.out.println("Number of enrolled students: " + entry.getValue());
      } else {
        System.out.println("\nNo courses found");
      }

      // All students in each course
      for (Course course : courseService.getAll()) {
        Optional<Map.Entry<Course, List<Student>>> studentsInCourse =
            analytics.getStudentsInCourse(course.getId());
        if (studentsInCourse.isPresent()) {
          Map.Entry<Course, List<Student>> entry = studentsInCourse.get();
          System.out.println("\nStudents enrolled in " + entry.getKey().getName() + ":");
          for (Student s : entry.getValue()) {
            System.out.println("- " + s.getName() + " (" + s.getEmail() + ")");
          }
        }
      }

    } catch (DatabaseException e) {
      System.out.println("Error running analytics: " + e.getMessage());
    }
  }
}