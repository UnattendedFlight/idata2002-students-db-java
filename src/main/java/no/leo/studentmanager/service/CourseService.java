package no.leo.studentmanager.service;

import java.util.List;
import no.leo.studentmanager.exception.DatabaseException;
import no.leo.studentmanager.model.Course;

/**
 * Service class for managing Course entities.
 * This class provides functionalities for database operations
 * related to Course entities, such as retrieving, querying,
 * and managing course data.
 */
public class CourseService extends BaseService<Course> {

  public CourseService(String dbPath) {
    super(Course.class, dbPath);
  }

  /**
   * Retrieves the name of the database table associated with the Course entity.
   *
   * @return the name of the database table, which is "courses".
   */
  @Override
  protected String getTableName() {
    return "courses";
  }

  /**
   * Retrieves a Course entity by its name.
   * This method queries the database for courses matching the specified name,
   * and ensures there is exactly one match. If no matches are found,
   * the method returns null. If multiple matches are found,
   * a DatabaseException is thrown.
   *
   * @param name the name of the course to retrieve
   * @return the Course object with the specified name, or null if no match is found
   * @throws DatabaseException if multiple courses are found with the specified name
   */
  public Course getByName(String name) throws DatabaseException {
    List<Course> courses = this.getByField("name", name);
    if (courses.isEmpty()) {
      return null;
    }
    if (courses.size() > 1) {
      throw new DatabaseException("Multiple courses found with name: " + name);
    }
    return courses.getFirst();
  }
}