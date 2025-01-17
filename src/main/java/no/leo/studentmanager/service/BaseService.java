package no.leo.studentmanager.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import no.leo.studentmanager.exception.DatabaseException;
import no.leo.studentmanager.exception.RecordNotFoundException;
import no.leo.studentmanager.exception.UniqueConstraintViolation;

/**
 * Abstract base class for data services, providing common functionality
 * for managing records, unique constraints, and field-based indices.
 *
 * @param <T> The type of the model this service manages.
 */
public abstract class BaseService<T> {
  protected final Class<T> modelClass;
  protected final Path dbPath;
  protected final ObjectMapper objectMapper;
  protected Map<String, Object> data;
  protected Map<String, Object> definition;

  /**
   * Constructs a new instance of BaseService.
   * This acts as the base class for services handling data models.
   * Initializes the database directory, definitions, and data required for operations.
   *
   * @param modelClass the class type of the model handled by the service
   * @param dbPath     the file system path to the database directory
   */
  @SuppressWarnings("unchecked")
  public BaseService(Class<T> modelClass, String dbPath) {
    this.modelClass = modelClass;
    this.dbPath = Paths.get(dbPath);
    this.objectMapper = new ObjectMapper();
    ensureDbDirectory();
    this.definition = loadDefinition();
    this.data = loadData();

    // Initialize data and indices if they don't exist
    if (!this.data.containsKey("data")) {
      this.data.put("data", new HashMap<>());
    }
    if (!this.data.containsKey("indices")) {
      this.data.put("indices", new HashMap<>());
    }
  }

  /**
   * Retrieves the name of the database table associated with the specific service implementation.
   * Each subclass must provide the table name corresponding to the entities it manages.
   *
   * @return the name of the database table as a String.
   */
  protected abstract String getTableName();

  protected void ensureDbDirectory() {
    try {
      Files.createDirectories(this.dbPath);
    } catch (IOException e) {
      throw new RuntimeException("Could not create database directory", e);
    }
  }

  /**
   * Loads the table definition for the database table associated with the specific service implementation.
   * The method reads the definitions from a JSON file and retrieves the definition of the table
   * corresponding to the name provided by the `getTableName` method. If the table definition is not found,
   * an exception is thrown.
   *
   * @return a map containing the table definition as key-value pairs.
   * This includes the table structure and metadata required for operations.
   * @throws RuntimeException if the table definitions file cannot be read or if the table definition
   *                          for the specified name is not found.
   */
  @SuppressWarnings("unchecked")
  protected Map<String, Object> loadDefinition() {
    try {
      String content = Files.readString(Paths.get("table_definitions.json"));
      Map<String, Object> root = this.objectMapper.readValue(content, Map.class);
      List<Map<String, Object>> tables = (List<Map<String, Object>>) root.get("tables");

      return tables.stream()
          .filter(table -> table.get("name").equals(this.getTableName()))
          .findFirst()
          .orElseThrow(() -> new RuntimeException("Table definition not found"));
    } catch (IOException e) {
      throw new RuntimeException("Could not load table definitions", e);
    }
  }

  /**
   * Loads the data for the specific database table associated with the service implementation.
   * This method attempts to read a JSON file corresponding to the table name.
   * If the file does not exist, it returns a new HashMap initialized with the table definition.
   * In case of I/O issues, a runtime exception is thrown.
   *
   * @return a map containing the loaded data as key-value pairs. If the data file does not exist,
   * a new map initialized with the table definition is returned.
   * @throws RuntimeException if an I/O error occurs while reading the data file.
   */
  @SuppressWarnings("unchecked")
  protected Map<String, Object> loadData() {
    Path filePath = this.dbPath.resolve(this.getTableName() + ".json");
    if (!Files.exists(filePath)) {
      return new HashMap<>(this.definition);
    }

    try {
      String content = Files.readString(filePath);
      return this.objectMapper.readValue(content, Map.class);
    } catch (IOException e) {
      throw new RuntimeException("Could not load data", e);
    }
  }

  /**
   * Persists the current state of the `data` map to a JSON file corresponding to the table name.
   * The method serializes the `data` map into a JSON string using a pretty printer and writes it to a file
   * in the specified database path. The file is named after the table name given by `getTableName` with a `.json` extension.
   * <p>
   * If an error occurs during the writing process, a `RuntimeException` is thrown.
   *
   * @throws RuntimeException if an I/O error occurs during the writing of the JSON data to file.
   */
  protected void saveData() {
    try {
      String json = this.objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(data);
      Files.writeString(this.dbPath.resolve(this.getTableName() + ".json"), json);
    } catch (IOException e) {
      throw new RuntimeException("Could not save data", e);
    }
  }

  /**
   * Retrieves the nested data map from the `data` field.
   * The `data` field is expected to contain a key "data" that maps to a nested map structure.
   *
   * @return a map containing nested maps as values. The outer map uses a string as the key,
   * and each inner map contains keys and values of type string and object, respectively.
   */
  @SuppressWarnings("unchecked")
  protected Map<String, Map<String, Object>> getDataMap() {
    return (Map<String, Map<String, Object>>) this.data.get("data");
  }

  /**
   * Retrieves the indices map associated with the data object.
   * The indices map is a nested map structure where the outer map
   * uses a string as the key, typically representing the index name,
   * and the inner map contains keys and values of generic types.
   *
   * @return a map containing the indices structure. The outer map has string keys
   * and the inner maps contain key-value pairs with Object types.
   */
  @SuppressWarnings("unchecked")
  protected Map<String, Map<Object, Object>> getIndicesMap() {
    return (Map<String, Map<Object, Object>>) this.data.get("indices");
  }

  /**
   * Generates the next sequential identifier based on the existing records in the data map.
   * The method retrieves all current record keys, parses them as integers, and determines the maximum value.
   * If no records exist, it returns 1 as the starting identifier.
   *
   * @return the next available integer identifier for a new record.
   */
  protected int getNextId() {
    Map<String, ?> records = this.getDataMap();
    if (records.isEmpty()) {
      return 1;
    }
    return records.keySet().stream()
        .mapToInt(Integer::parseInt)
        .max()
        .getAsInt() + 1;
  }

  /**
   * Validates that the unique constraints for the fields in the given record are not violated.
   * Inspects the fields of the provided record to ensure that any field marked with a unique constraint
   * does not have a value conflicting with existing records in the database. If a conflict is found
   * and the offending record ID is not equal to the provided excludeId, a {@code UniqueConstraintViolation} is thrown.
   *
   * @param record    the record object to validate for unique constraint compliance
   * @param excludeId the ID of an existing record to exclude from validation to avoid self-conflicts (can be null)
   * @throws DatabaseException if a unique constraint violation is detected
   */
  @SuppressWarnings("unchecked")
  protected void validateUniqueConstraints(T record, Integer excludeId) throws DatabaseException {
    Map<String, Object> recordMap = this.objectMapper.convertValue(record, Map.class);
    Map<String, Object> definitions = (Map<String, Object>) this.definition.get("definitions");

    for (Map.Entry<String, Object> entry : definitions.entrySet()) {
      String fieldName = entry.getKey();
      Map<String, Object> fieldDef = (Map<String, Object>) entry.getValue();
      Map<String, Object> constraints = (Map<String, Object>) fieldDef.get("constraints");

      if (constraints != null && Boolean.TRUE.equals(constraints.get("unique"))) {
        Object fieldValue = recordMap.get(fieldName);
        String indexName = fieldName + "_id_idx";

        if (this.getIndicesMap().containsKey(indexName)) {
          Object existingId = this.getIndicesMap().get(indexName).get(fieldValue);
          if (existingId != null && (!existingId.equals(excludeId))) {
            throw new UniqueConstraintViolation(
                fieldName + " " + fieldValue + " already exists"
            );
          }
        }
      }
    }
  }

  /**
   * Updates the indices map with the given record and its corresponding ID.
   * The method dynamically updates the index entries for each field of the
   * given record by associating field values with the given record ID. If
   * the field is unique, the index maps the value directly to the record ID.
   * Otherwise, it maintains a list of IDs for the corresponding field value.
   *
   * @param record   the record to update indices for, represented as a generic type
   * @param recordId the ID of the record to associate with the indexed values
   */
  protected void updateIndices(T record, int recordId) {
    Map<String, Object> recordMap = this.objectMapper.convertValue(record, Map.class);
    Map<String, Map<Object, Object>> indices = this.getIndicesMap();

    for (String fieldName : recordMap.keySet()) {
      String indexName = fieldName + "_id_idx";
      if (!indices.containsKey(indexName)) {
        continue;
      }

      Object fieldValue = recordMap.get(fieldName);
      indices.computeIfAbsent(indexName, k -> new HashMap<>());
      Map<Object, Object> index = indices.get(indexName);

      if (this.isUniqueField(fieldName)) {
        index.put(fieldValue, recordId);
      } else {
        List<Integer> ids;
        if (index.containsKey(fieldValue)) {
          ids = index.get(fieldValue) instanceof List ?
              (List<Integer>) index.get(fieldValue) :
              new ArrayList<>(Collections.singletonList((Integer) index.get(fieldValue)));
        } else {
          ids = new ArrayList<>();
        }
        if (!ids.contains(recordId)) {
          ids.add(recordId);
        }
        index.put(fieldValue, ids);
      }
    }
  }

  /**
   * Checks if the specified field is marked as unique in the table definition.
   * This method looks up the field in the table's definitions, retrieves its constraints,
   * and determines if the "unique" constraint is set to true.
   *
   * @param fieldName the name of the field to check for uniqueness
   * @return true if the field is marked as unique in the table definition, false otherwise
   */
  @SuppressWarnings("unchecked")
  protected boolean isUniqueField(String fieldName) {
    Map<String, Object> definitions = (Map<String, Object>) this.definition.get("definitions");
    Map<String, Object> fieldDef = (Map<String, Object>) definitions.get(fieldName);
    if (fieldDef != null) {
      Map<String, Object> constraints = (Map<String, Object>) fieldDef.get("constraints");
      return constraints != null && Boolean.TRUE.equals(constraints.get("unique"));
    }
    return false;
  }

  /**
   * Removes the specified record from all applicable indices.
   * This method ensures that the indices are updated to remove any references to the record
   * identified by the given record ID. It supports both unique and non-unique fields in the indices.
   *
   * @param recordId The ID of the record to be removed from the indices.
   * @throws RecordNotFoundException If no record is found for the given record ID.
   */
  protected void removeFromIndices(int recordId) throws RecordNotFoundException {
    T record = this.getById(recordId);
    Map<String, Object> recordMap = this.objectMapper.convertValue(record, Map.class);
    Map<String, Map<Object, Object>> indices = this.getIndicesMap();

    for (String fieldName : recordMap.keySet()) {
      String indexName = fieldName + "_id_idx";
      if (!indices.containsKey(indexName)) {
        continue;
      }

      Object fieldValue = recordMap.get(fieldName);
      Map<Object, Object> index = indices.get(indexName);

      if (this.isUniqueField(fieldName)) {
        index.remove(fieldValue);
      } else {
        Object indexValue = index.get(fieldValue);
        if (indexValue instanceof List) {
          ((List<Integer>) indexValue).remove(Integer.valueOf(recordId));
          if (((List<Integer>) indexValue).isEmpty()) {
            index.remove(fieldValue);
          }
        } else if (indexValue instanceof Integer && (Integer) indexValue == recordId) {
          index.remove(fieldValue);
        }
      }
    }
  }

  /**
   * Creates a new record of the specified type and inserts it into the database.
   * This method validates the record against unique constraints, assigns it a new unique identifier,
   * updates the relevant indices, and persists the changes.
   *
   * @param record the record object to be created and stored in the database
   * @return the created record object with the assigned identifier
   * @throws DatabaseException if any error occurs during the creation process, such as a unique constraint violation
   */
  public T create(T record) throws DatabaseException {
    this.validateUniqueConstraints(record, null);

    int recordId = this.getNextId();
    Map<String, Object> recordMap = this.objectMapper.convertValue(record, Map.class);
    recordMap.put("id", recordId);

    this.getDataMap().put(String.valueOf(recordId), recordMap);
    T createdRecord = this.objectMapper.convertValue(recordMap, this.modelClass);
    this.updateIndices(createdRecord, recordId);
    this.saveData();

    return createdRecord;
  }

  /**
   * Retrieves a record from the database based on the provided ID.
   * If no record is found with the specified ID, a {@code RecordNotFoundException} is thrown.
   *
   * @param id the unique identifier of the record to retrieve
   * @return an instance of the generic type {@code T} representing the retrieved record
   * @throws RecordNotFoundException if a record with the given ID does not exist
   */
  public T getById(int id) throws RecordNotFoundException {
    Map<String, Object> recordData = this.getDataMap().get(String.valueOf(id));
    if (recordData == null) {
      throw new RecordNotFoundException("Record with ID " + id + " not found");
    }
    return this.objectMapper.convertValue(recordData, this.modelClass);
  }

  /**
   * Retrieves a list of records filtered by a specified field and its value.
   * If an index exists for the given field, the method uses the index for optimized lookups.
   * Otherwise, it performs a full scan of the data to find matching records.
   *
   * @param fieldName the name of the field to filter the records by
   * @param value     the value to match against the specified field
   * @return a list of records of type {@code T} that match the specified field and value.
   * If no matching records are found, an empty list is returned.
   */
  @SuppressWarnings("unchecked")
  public List<T> getByField(String fieldName, Object value) {
    String indexName = fieldName + "_id_idx";
    Map<String, Map<Object, Object>> indices = this.getIndicesMap();

    if (indices.containsKey(indexName)) {
      Map<Object, Object> index = indices.get(indexName);
      Object indexValue = index.get(value);

      if (indexValue == null) {
        return new ArrayList<>();
      }

      List<Integer> ids = indexValue instanceof List ?
          (List<Integer>) indexValue :
          Collections.singletonList((Integer) indexValue);

      List<T> results = new ArrayList<>();
      for (Integer id : ids) {
        try {
          results.add(getById(id));
        } catch (RecordNotFoundException e) {
          // Skip invalid records
        }
      }
      return results;
    }

    // Fallback to full scan if no index exists
    return this.getDataMap().values().stream()
        .filter(record -> Objects.equals(record.get(fieldName), value))
        .map(record -> this.objectMapper.convertValue(record, this.modelClass))
        .toList();
  }

  /**
   * Retrieves all the records from the data storage as a list of objects of type {@code T}.
   * The method converts each record from its raw representation in the data map
   * to instances of the {@code T} class using the object mapper.
   *
   * @return a list of objects of type {@code T}, representing all records in the data storage.
   */
  public List<T> getAll() {
    return this.getDataMap().values().stream()
        .map(record -> this.objectMapper.convertValue(record, this.modelClass))
        .toList();
  }

  /**
   * Updates an existing record in the database. Validates unique constraints,
   * removes the record from existing indices, updates database entries, and
   * applies the updated record to the indices and data store.
   *
   * @param record the record to update, represented as an instance of the generic type {@code T}
   * @return the updated record object
   * @throws DatabaseException       if an error occurs during the update process, such as a unique constraint violation
   * @throws RecordNotFoundException if the specified record ID does not exist in the database
   */
  public T update(T record) throws DatabaseException {
    Map<String, Object> recordMap = this.objectMapper.convertValue(record, Map.class);
    int recordId = (Integer) recordMap.get("id");

    if (!getDataMap().containsKey(String.valueOf(recordId))) {
      throw new RecordNotFoundException("Record with ID " + recordId + " not found");
    }

    this.validateUniqueConstraints(record, recordId);
    this.removeFromIndices(recordId);
    this.getDataMap().put(String.valueOf(recordId), recordMap);
    this.updateIndices(record, recordId);
    this.saveData();

    return record;
  }

  /**
   * Deletes a record from the database using the specified unique identifier.
   * If the record with the provided ID does not exist, a {@code RecordNotFoundException} is thrown.
   * This method also removes the record's references from all applicable indices
   * and persists the updated data state.
   *
   * @param id the unique identifier of the record to be deleted
   * @throws DatabaseException       if an error occurs while deleting the record
   * @throws RecordNotFoundException if the record with the specified ID does not exist
   */
  public void delete(int id) throws DatabaseException {
    if (!getDataMap().containsKey(String.valueOf(id))) {
      throw new RecordNotFoundException("Record with ID " + id + " not found");
    }

    this.removeFromIndices(id);
    this.getDataMap().remove(String.valueOf(id));
    this.saveData();
  }
}