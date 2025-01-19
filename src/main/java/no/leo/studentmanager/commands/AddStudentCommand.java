package no.leo.studentmanager.commands;

import no.leo.studentmanager.model.Student;
import no.leo.studentmanager.service.CourseEnrollmentService;
import no.leo.studentmanager.service.CourseService;
import no.leo.studentmanager.service.StudentAnalytics;
import no.leo.studentmanager.service.StudentService;

public class AddStudentCommand extends AbstractCommand {
  public AddStudentCommand(StudentService studentService,
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

    if (args.length < 3) {
      System.out.println("Error: " + getUsage());
      return;
    }

    Student student = new Student(0, args[0], args[1], args[2]);
    Student created = studentService.create(student);
    System.out.println("Created student: " + created);
  }

  @Override
  public String getDescription() {
    return "Add a new student to the system";
  }

  @Override
  public String getUsage() {
    return this.runtimeCommandString + " <name> <email> <phone> - Create a new student with the given details";
  }
}
