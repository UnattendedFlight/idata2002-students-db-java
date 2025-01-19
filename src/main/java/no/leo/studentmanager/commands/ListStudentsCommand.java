package no.leo.studentmanager.commands;
import no.leo.studentmanager.model.Student;
import java.util.List;
import no.leo.studentmanager.service.CourseEnrollmentService;
import no.leo.studentmanager.service.CourseService;
import no.leo.studentmanager.service.StudentAnalytics;
import no.leo.studentmanager.service.StudentService;

public class ListStudentsCommand extends AbstractCommand {
  public ListStudentsCommand(StudentService studentService,
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

    List<Student> students = studentService.getAll();
    if (students.isEmpty()) {
      System.out.println("No students found.");
      return;
    }

    for (Student student : students) {
      System.out.printf("ID: %d, Name: %s, Email: %s, Phone: %s%n",
          student.getId(), student.getName(), student.getEmail(), student.getPhone());
    }
  }

  @Override
  public String getDescription() {
    return "List all students in the system";
  }

  @Override
  public String getUsage() {
    return this.runtimeCommandString + " - Lists all students currently in the system";
  }
}
