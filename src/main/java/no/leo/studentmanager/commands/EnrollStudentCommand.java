package no.leo.studentmanager.commands;

import no.leo.studentmanager.service.CourseEnrollmentService;
import no.leo.studentmanager.service.CourseService;
import no.leo.studentmanager.service.StudentAnalytics;
import no.leo.studentmanager.service.StudentService;

public class EnrollStudentCommand extends AbstractCommand {
  public EnrollStudentCommand(StudentService studentService,
                              CourseService courseService,
                              CourseEnrollmentService enrollmentService,
                              StudentAnalytics analytics) {
    super(studentService, courseService, enrollmentService, analytics);
  }

  @Override
  public void execute(String[] args) throws Exception {
    if (args.length < 2) {
      System.out.println("Error: " + getUsage());
      return;
    }

    int studentId = Integer.parseInt(args[0]);
    int courseId = Integer.parseInt(args[1]);
    enrollmentService.enrollStudent(studentId, courseId);
    System.out.println("Enrolled student in course");
  }

  @Override
  public String getDescription() {
    return "Enroll a student in a course";
  }

  @Override
  public String getUsage() {
    return this.runtimeCommandString + " <student_id> <course_id> - Enroll a student in a course";
  }
}
