package no.leo.studentmanager.commands;

public class HelpCommand implements Command {
  private final CommandRegistry registry;
  private String runtimeCommandString = "not-set";

  public HelpCommand(CommandRegistry registry) {
    this.registry = registry;
  }

  @Override
  public void execute(String[] args) {
    System.out.println("Available commands:");
    registry.getAllCommands().forEach((name, command) ->
        System.out.printf("  %-20s - %s%n", name, command.getDescription()));
    System.out.println("\nType '<command> help' for detailed usage information.");
  }

  @Override
  public String getDescription() {
    return "Display help information about available commands";
  }

  @Override
  public String getUsage() {
    return runtimeCommandString + " - Display help information about available commands";
  }

  @Override
  public void setRuntimeCommandString(String name) {
    this.runtimeCommandString = name;
  }
}
