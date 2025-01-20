package no.leo.studentmanager.commands;

import java.util.List;
import java.util.Optional;
import no.leo.studentmanager.exception.RecordNotFoundException;
import no.leo.studentmanager.model.Course;
import no.leo.studentmanager.model.CourseEnrollment;
import no.leo.studentmanager.model.Student;
import no.leo.studentmanager.service.CourseEnrollmentService;
import no.leo.studentmanager.service.CourseService;
import no.leo.studentmanager.service.StudentAnalytics;
import no.leo.studentmanager.service.StudentService;

public class GetEnrollmentCommand extends AbstractCommand {
  public GetEnrollmentCommand(StudentService studentService,
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

    if (args.length < 1) {
      System.out.println("Error: " + getUsage());
      return;
    }
    try {
      int enrollmentId = Integer.parseInt(args[0]);
      CourseEnrollment enrollment = enrollmentService.getById(enrollmentId);
      if (enrollment == null) {
        System.out.println("No enrollment found with ID " + enrollmentId);
        return;
      }
      Student student = studentService.getById(enrollment.getStudentId());
      Course course = courseService.getById(enrollment.getCourseId());

      StringBuilder sb = new StringBuilder();
      sb.append("Enrollment ID: ").append(enrollment.getId()).append("\n");
      sb.append("Student ID: ").append(student.getId()).append("\n");
      sb.append("Student Name: ").append(student.getName()).append("\n");
      sb.append("Course ID: ").append(course.getId()).append("\n");
      sb.append("Course Name: ").append(course.getName()).append("\n");
      sb.append("Grade: ").append(enrollment.getGrade());
      System.out.println(sb);
      return;
    } catch (NumberFormatException e) {
      throw new RuntimeException(e);
    } catch (RecordNotFoundException e) {
      throw new RuntimeException(e);
    }
  }

    @Override
  public String getDescription() {
    return "Get enrollment details";
  }

  @Override
  public String getUsage() {
    return this.runtimeCommandString + " <enrollment_id> - Get enrollment details";
  }
}
