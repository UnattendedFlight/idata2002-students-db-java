package no.leo.studentmanager.commands;

public interface Command {
  void execute(String[] args) throws Exception;
  String getDescription();
  String getUsage();

  void setRuntimeCommandString(String name);

  String getRuntimeCommandString();
}
