package no.leo.studentmanager.commands;

import java.util.List;
import no.leo.studentmanager.model.Course;
import no.leo.studentmanager.service.CourseEnrollmentService;
import no.leo.studentmanager.service.CourseService;
import no.leo.studentmanager.service.StudentAnalytics;
import no.leo.studentmanager.service.StudentService;

public class ListCoursesCommand extends AbstractCommand {
  public ListCoursesCommand(StudentService studentService,
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

    List<Course> courses = courseService.getAll();
    if (courses.isEmpty()) {
      System.out.println("No courses found.");
      return;
    }

    for (Course course : courses) {
      System.out.printf("ID: %d, Name: %s%n",
          course.getId(), course.getName());
    }
  }

  @Override
  public String getDescription() {
    return "List all courses in the system";
  }

  @Override
  public String getUsage() {
    return this.runtimeCommandString + " - Lists all courses currently in the system";
  }
}
