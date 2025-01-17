package no.leo.studentmanager.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Objects;

/**
 * Represents a Student entity with an ID, name, email, and phone.
 * <p>
 * This class is used for managing student data, typically in the context
 * of the student management system. The class includes annotations to
 * support JSON serialization and deserialization using the Jackson library.
 * <p>
 * Instances of this class can be compared using the equals method,
 * which checks equality based on the ID, name, email, and phone attributes.
 * <p>
 * The hashCode method ensures that instances with the same attributes
 * produce consistent hash values for use in hash-based collections.
 */
public class Student {
  @JsonProperty("id")
  private int id;
  @JsonProperty("name")
  private String name;
  @JsonProperty("email")
  private String email;
  @JsonProperty("phone")
  private String phone;

  // JsonCreator constructor for Jackson
  @JsonCreator
  public Student(
      @JsonProperty("id") int id,
      @JsonProperty("name") String name,
      @JsonProperty("email") String email,
      @JsonProperty("phone") String phone) {
    this.id = id;
    this.name = name;
    this.email = email;
    this.phone = phone;
  }

  /**
   * Retrieves the ID of the student.
   *
   * @return the unique identifier associated with the student
   */
  public int getId() {
    return id;
  }

  /**
   * Sets the ID for the student.
   *
   * @param id the unique identifier to be assigned to the student
   */
  public void setId(int id) {
    this.id = id;
  }

  /**
   * Retrieves the name of the student.
   *
   * @return the name associated with the student
   */
  public String getName() {
    return name;
  }

  /**
   * Sets the name for the student.
   *
   * @param name the name to be assigned to the student
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * Retrieves the email address of the student.
   *
   * @return the email address associated with the student
   */
  public String getEmail() {
    return email;
  }

  /**
   * Sets the email address associated with the student.
   *
   * @param email the email address to be assigned to the student
   */
  public void setEmail(String email) {
    this.email = email;
  }

  /**
   * Retrieves the phone number of the student.
   *
   * @return the phone number associated with the student
   */
  public String getPhone() {
    return phone;
  }

  /**
   * Updates the phone number of the student.
   *
   * @param phone the new phone number to set for the student
   */
  public void setPhone(String phone) {
    this.phone = phone;
  }

  /**
   * Returns a string representation of the Student object.
   * The string includes the values of the id, name, email, and phone attributes.
   *
   * @return a string representation of the object in the format "Student{id=value, name='value', email='value', phone='value'}"
   */
  @Override
  public String toString() {
    return "Student{" +
        "id=" + id +
        ", name='" + name + '\'' +
        ", email='" + email + '\'' +
        ", phone='" + phone + '\'' +
        '}';
  }

  /**
   * Compares this Student object with another object to determine equality.
   * The comparison is based on the `id`, `name`, `email`, and `phone` fields of the Student object.
   *
   * @param o the object to compare with this Student object
   * @return true if the specified object is of type Student and has the same `id`, `name`, `email`, and `phone`; false otherwise
   */
  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Student student = (Student) o;
    return id == student.id &&
        Objects.equals(name, student.name) &&
        Objects.equals(email, student.email) &&
        Objects.equals(phone, student.phone);
  }

  /**
   * Generates a hash code for the current instance of the Student object.
   * The hash code is computed based on the `id`, `name`, `email`, and `phone` fields of the object.
   *
   * @return an integer hash code value for the Student object
   */
  @Override
  public int hashCode() {
    return Objects.hash(id, name, email, phone);
  }
}