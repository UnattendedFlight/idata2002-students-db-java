package no.leo.studentmanager.commands;

import no.leo.studentmanager.model.Course;
import no.leo.studentmanager.service.CourseEnrollmentService;
import no.leo.studentmanager.service.CourseService;
import no.leo.studentmanager.service.StudentAnalytics;
import no.leo.studentmanager.service.StudentService;

public class AddCourseCommand extends AbstractCommand {
  public AddCourseCommand(StudentService studentService,
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

    // Join all arguments to support course names with spaces
    String courseName = String.join(" ", args);
    Course course = new Course(0, courseName);
    Course created = courseService.create(course);
    System.out.println("Created course: " + created);
  }

  @Override
  public String getDescription() {
    return "Add a new course to the system";
  }

  @Override
  public String getUsage() {
    return this.runtimeCommandString + " <name> - Create a new course with the given name";
  }
}