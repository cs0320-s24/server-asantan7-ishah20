package edu.brown.cs.student.main.csv.CreatorFromRowExamples.Student;

import edu.brown.cs.student.main.csv.CreatorFromRowExamples.CreatorFromRow;
import edu.brown.cs.student.main.csv.CreatorFromRowExamples.Student.Student;
import java.util.List;

public class StudentCreator implements CreatorFromRow<Student> {
  @Override
  public Student create(List<String> row) {
    // Assuming the row format matches the Student constructor
    // Example: "John Doe,30,Developer"
    if (row.size() < 3) {
      throw new RuntimeException("Row does not have enough data to create a Student");
    }
    String name = row.get(0);
    int age = Integer.parseInt(row.get(1));
    String occupation = row.get(2);
    return new Student(name, age, occupation);
  }

  @Override
  public List<String> toListOfString(Student object) {
    return (List<String>) object;
  }
}
