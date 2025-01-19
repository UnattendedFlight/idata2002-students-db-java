package no.leo.studentmanager.commands;


import no.leo.studentmanager.service.StudentService;
import no.leo.studentmanager.service.CourseService;
import no.leo.studentmanager.service.CourseEnrollmentService;
import no.leo.studentmanager.service.StudentAnalytics;

public abstract class AbstractCommand implements Command {
  protected final StudentService studentService;
  protected final CourseService courseService;
  protected final CourseEnrollmentService enrollmentService;
  protected final StudentAnalytics analytics;
  protected String runtimeCommandString = "not-set";

  protected AbstractCommand(StudentService studentService,
                            CourseService courseService,
                            CourseEnrollmentService enrollmentService,
                            StudentAnalytics analytics) {
    this.studentService = studentService;
    this.courseService = courseService;
    this.enrollmentService = enrollmentService;
    this.analytics = analytics;
  }

  public void setRuntimeCommandString(String runtimeCommandString) {
    this.runtimeCommandString = runtimeCommandString;
  }
}
