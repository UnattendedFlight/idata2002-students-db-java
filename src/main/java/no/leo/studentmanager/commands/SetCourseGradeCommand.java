package no.leo.studentmanager.commands;

import no.leo.studentmanager.service.CourseEnrollmentService;
import no.leo.studentmanager.service.CourseService;
import no.leo.studentmanager.service.StudentAnalytics;
import no.leo.studentmanager.service.StudentService;

public class SetCourseGradeCommand extends AbstractCommand {
  public SetCourseGradeCommand(StudentService studentService,
                               CourseService courseService,
                               CourseEnrollmentService enrollmentService,
                               StudentAnalytics analytics) {
    super(studentService, courseService, enrollmentService, analytics);
  }

  @Override
  public void execute(String[] args) throws Exception {
    if (args.length < 3) {
      System.out.println("Error: " + getUsage());
      return;
    }

    int studentId = Integer.parseInt(args[0]);
    int courseId = Integer.parseInt(args[1]);
    int grade = Integer.parseInt(args[2]);
    enrollmentService.setGrade(studentId, courseId, grade);
    System.out.println("Set grade for student in course");
  }

  @Override
  public String getDescription() {
    return "Set a grade for a student in a course";
  }

  @Override
  public String getUsage() {
    return this.runtimeCommandString + " <student_id> <course_id> <grade> - Set a grade for a student in a course";
  }
}
