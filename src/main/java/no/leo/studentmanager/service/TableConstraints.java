package no.leo.studentmanager.service;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

public class TableConstraints {
  private static Map<String, Object> loadTableDefinitions(String tableName) {
    try {
      String content = Files.readString(Paths.get("table_definitions.json"));
      ObjectMapper mapper = new ObjectMapper();
      Map<String, Object> root = mapper.readValue(content, Map.class);

      for (Map<String, Object> table : (List<Map<String, Object>>) root.get("tables")) {
        if (table.get("name").equals(tableName)) {
          return table;
        }
      }
      throw new RuntimeException("Table definition not found: " + tableName);
    } catch (IOException e) {
      throw new RuntimeException("Could not load table definitions", e);
    }
  }

  @SuppressWarnings("unchecked")
  private static Map<String, Object> getFieldConstraints(String tableName, String fieldName) {
    Map<String, Object> table = loadTableDefinitions(tableName);
    Map<String, Object> definitions = (Map<String, Object>) table.get("definitions");
    Map<String, Object> field = (Map<String, Object>) definitions.get(fieldName);
    return field != null ? (Map<String, Object>) field.get("constraints") : null;
  }

  public static void validateField(String tableName, String fieldName, Object value) {
    Map<String, Object> constraints = getFieldConstraints(tableName, fieldName);
    if (constraints == null) {
      return;
    }

    // Check not null constraint
    if (Boolean.TRUE.equals(constraints.get("not_null")) && value == null) {
      throw new IllegalArgumentException(fieldName + " cannot be null");
    }

    if (value == null) {
      return;
    }

    // Check length constraint for strings
    if (constraints.containsKey("length") && value instanceof String) {
      int maxLength = ((Integer) constraints.get("length"));
      if (((String) value).length() != maxLength) {
        throw new IllegalArgumentException(
            String.format("%s must be exactly %d characters long", fieldName, maxLength));
      }
    }

    // Check range constraint for integers
    if (constraints.containsKey("min") && value instanceof Integer) {
      int min = (Integer) constraints.get("min");
      if ((Integer) value < min) {
        throw new IllegalArgumentException(
            String.format("%s must be at least %d", fieldName, min));
      }
    }

    if (constraints.containsKey("max") && value instanceof Integer) {
      int max = (Integer) constraints.get("max");
      if ((Integer) value > max) {
        throw new IllegalArgumentException(
            String.format("%s must be at most %d", fieldName, max));
      }
    }
  }

  public static void validateRecord(String tableName, Map<String, Object> record) {
    Map<String, Object> table = loadTableDefinitions(tableName);
    Map<String, Object> definitions = (Map<String, Object>) table.get("definitions");

    for (Map.Entry<String, Object> field : ((Map<String, Object>) definitions).entrySet()) {
      String fieldName = field.getKey();
      validateField(tableName, fieldName, record.get(fieldName));
    }
  }
}
