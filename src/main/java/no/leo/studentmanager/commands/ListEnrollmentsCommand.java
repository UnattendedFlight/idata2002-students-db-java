package no.leo.studentmanager.commands;

import java.util.List;
import no.leo.studentmanager.model.Course;
import no.leo.studentmanager.model.CourseEnrollment;
import no.leo.studentmanager.model.Student;
import no.leo.studentmanager.service.CourseEnrollmentService;
import no.leo.studentmanager.service.CourseService;
import no.leo.studentmanager.service.StudentAnalytics;
import no.leo.studentmanager.service.StudentService;

public class ListEnrollmentsCommand extends AbstractCommand {
  public ListEnrollmentsCommand(StudentService studentService,
                                CourseService courseService,
                                CourseEnrollmentService enrollmentService,
                                StudentAnalytics analytics) {
    super(studentService, courseService, enrollmentService, analytics);
  }

  @Override
  public void execute(String[] args) throws Exception {
    if (args.length > 0 && args[0].equals("help")) {
      System.out.println(getUsage());
      return;
    }

    List<CourseEnrollment> enrollments;
    if (args.length == 1) {
      enrollments = getEnrollmentsForStudent(args[0]);
    } else {
      enrollments = enrollmentService.getAll();
    }

    displayEnrollments(enrollments, args.length == 1);
  }

  private List<CourseEnrollment> getEnrollmentsForStudent(String studentIdStr) throws Exception {
    try {
      int studentId = Integer.parseInt(studentIdStr);
      List<CourseEnrollment> enrollments = enrollmentService.getByStudent(studentId);
      if (enrollments.isEmpty()) {
        System.out.println("No enrollments found for student with ID " + studentId);
      }
      return enrollments;
    } catch (NumberFormatException e) {
      System.out.println("Error: " + getUsage());
      return List.of(); // Return empty list instead of null
    }
  }

  private void displayEnrollments(List<CourseEnrollment> enrollments, boolean studentSpecific) throws Exception {
    if (enrollments.isEmpty()) {
      System.out.println("No enrollments found.");
      return;
    }

    for (CourseEnrollment enrollment : enrollments) {
      if (studentSpecific) {
        displayStudentEnrollment(enrollment);
      } else {
        displayFullEnrollment(enrollment);
      }
    }
  }

  private void displayStudentEnrollment(CourseEnrollment enrollment) throws Exception {
    Course course = courseService.getById(enrollment.getCourseId());
    System.out.printf("ID: %d, Course: %s (ID: %d), Grade: %d%n",
        enrollment.getId(),
        course.getName(),
        course.getId(),
        enrollment.getGrade());
  }

  private void displayFullEnrollment(CourseEnrollment enrollment) throws Exception {
    Student student = studentService.getById(enrollment.getStudentId());
    Course course = courseService.getById(enrollment.getCourseId());
    System.out.printf("ID: %d, Student: %s (ID: %d), Course: %s (ID: %d), Grade: %d%n",
        enrollment.getId(),
        student.getName(),
        student.getId(),
        course.getName(),
        course.getId(),
        enrollment.getGrade());
  }

  @Override
  public String getDescription() {
    return "List all course enrollments in the system";
  }

  @Override
  public String getUsage() {
    return this.runtimeCommandString + " - Lists all course enrollments with student and course details";
  }
}
