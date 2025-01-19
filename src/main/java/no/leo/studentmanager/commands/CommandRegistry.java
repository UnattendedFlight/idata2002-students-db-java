package no.leo.studentmanager.commands;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class CommandRegistry {
  private final Map<String, Command> commands = new HashMap<>();

  public void registerCommand(String name, Command command) {
    commands.put(name.toLowerCase(), command);
    command.setRuntimeCommandString(name);
  }

  public Command getCommand(String name) {
    return commands.get(name.toLowerCase());
  }

  public Set<String> getCommandNames() {
    return commands.keySet();
  }

  public Map<String, Command> getAllCommands() {
    return new HashMap<>(commands);
  }
}
