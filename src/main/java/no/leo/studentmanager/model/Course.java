package no.leo.studentmanager.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Objects;

/**
 * Represents a Course entity with an ID and a name.
 * <p>
 * This class is used for managing course data, typically in the context of a student
 * management system. The class includes annotations to support JSON serialization
 * and deserialization using the Jackson library.
 * <p>
 * Instances of this class can be compared using the equals method, which checks
 * equality based on the ID and name attributes.
 * <p>
 * The hashCode method ensures that instances with the same ID and name produce
 * consistent hash values for use in hash-based collections.
 */
public class Course {
  @JsonProperty("id")
  private int id;
  @JsonProperty("name")
  private String name;

  // Default constructor for Jackson
  public Course() {
  }

  // JsonCreator constructor for Jackson
  @JsonCreator
  public Course(
      @JsonProperty("id") int id,
      @JsonProperty("name") String name) {
    this.id = id;
    this.name = name;
  }

  /**
   * Retrieves the ID of the object.
   *
   * @return the unique identifier associated with the object
   */
  public int getId() {
    return id;
  }

  /**
   * Sets the ID for the object.
   *
   * @param id the unique identifier to be assigned to the object
   */
  public void setId(int id) {
    this.id = id;
  }

  /**
   * Retrieves the name of the course.
   *
   * @return the name associated with the course
   */
  public String getName() {
    return name;
  }

  /**
   * Sets the name for the course.
   *
   * @param name the name to be assigned to the course
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * Returns a string representation of the Course object.
   * The string includes the values of the id and name attributes.
   *
   * @return a string representation of the object in the format "Course{id=value, name='value'}"
   */
  @Override
  public String toString() {
    return "Course{" +
        "id=" + id +
        ", name='" + name + '\'' +
        '}';
  }

  /**
   * Compares this Course object with another object to determine equality.
   * The comparison is based on the `id` and `name` fields of the Course object.
   *
   * @param o the object to compare with this Course object
   * @return true if the specified object is of type Course and has the same `id` and `name`; false otherwise
   */
  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Course course = (Course) o;
    return id == course.id &&
        Objects.equals(name, course.name);
  }

  /**
   * Generates a hash code for the current instance of the object.
   * The hash code is computed based on the `id` and `name` fields of the object.
   *
   * @return an integer hash code value for the object
   */
  @Override
  public int hashCode() {
    return Objects.hash(id, name);
  }
}