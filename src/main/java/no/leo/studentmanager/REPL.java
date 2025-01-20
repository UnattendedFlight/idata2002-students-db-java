package no.leo.studentmanager;

import java.util.Scanner;
import no.leo.studentmanager.commands.AddCourseCommand;
import no.leo.studentmanager.commands.AddStudentCommand;
import no.leo.studentmanager.commands.Command;
import no.leo.studentmanager.commands.CommandRegistry;
import no.leo.studentmanager.commands.EnrollStudentCommand;
import no.leo.studentmanager.commands.GetEnrollmentCommand;
import no.leo.studentmanager.commands.HelpCommand;
import no.leo.studentmanager.commands.ListCoursesCommand;
import no.leo.studentmanager.commands.ListEnrollmentsCommand;
import no.leo.studentmanager.commands.ListStudentsCommand;
import no.leo.studentmanager.commands.SetCourseGradeCommand;
import no.leo.studentmanager.service.CourseEnrollmentService;
import no.leo.studentmanager.service.CourseService;
import no.leo.studentmanager.service.StudentAnalytics;
import no.leo.studentmanager.service.StudentService;

public class REPL {
  private final CommandRegistry registry;
  private final Scanner scanner;

  public REPL() {
    this.registry = new CommandRegistry();
    this.scanner = new Scanner(System.in);
    initializeCommands();
  }

  private void initializeCommands() {
    StudentService studentService = new StudentService("db");
    CourseService courseService = new CourseService("db");
    CourseEnrollmentService enrollmentService = new CourseEnrollmentService("db");
    StudentAnalytics analytics = new StudentAnalytics();

    // Register all commands
    registry.registerCommand("help", new HelpCommand(registry));
    registry.registerCommand("student:list", new ListStudentsCommand(
        studentService, courseService, enrollmentService, analytics));
    registry.registerCommand("student:add", new AddStudentCommand(
        studentService, courseService, enrollmentService, analytics));
    registry.registerCommand("course:list", new ListCoursesCommand(
        studentService, courseService, enrollmentService, analytics));
    registry.registerCommand("course:add", new AddCourseCommand(
        studentService, courseService, enrollmentService, analytics));
    registry.registerCommand("enrollment:list", new ListEnrollmentsCommand(
        studentService, courseService, enrollmentService, analytics));
    registry.registerCommand("enrollment:add", new EnrollStudentCommand(
        studentService, courseService, enrollmentService, analytics));
    registry.registerCommand("course:grade:set", new SetCourseGradeCommand(
        studentService, courseService, enrollmentService, analytics));
    registry.registerCommand("enrollment:get", new GetEnrollmentCommand(
        studentService, courseService, enrollmentService, analytics));
  }

  public void start() {
    System.out.println("Welcome to Student Manager REPL!");
    System.out.println("Type 'help' for available commands");

    while (true) {
      System.out.print("> ");
      String input = scanner.nextLine().trim();

      if (input.equalsIgnoreCase("exit")) {
        break;
      }

      processCommand(input);
    }

    System.out.println("Goodbye!");
  }

  private void processCommand(String input) {
    String[] parts = input.split("\\s+", 2);
    String commandName = parts[0].toLowerCase();
    String[] args = parts.length > 1 ? parts[1].split("\\s+") : new String[0];

    Command command = registry.getCommand(commandName);
    if (command == null) {
      System.out.println("Unknown command. Type 'help' for available commands.");
      return;
    }

    try {
      command.execute(args);
    } catch (Exception e) {
      System.out.println("Error executing command: " + e.getMessage());
    }
  }

  public static void main(String[] args) {
    new REPL().start();
  }
}