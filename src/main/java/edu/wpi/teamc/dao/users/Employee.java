package edu.wpi.teamc.dao.users;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Employee extends AbsUser {

  private String department;
  private String position;

  public Employee(int id, String name, String department, String position) {
    super(id, name);
    this.department = department;
    this.position = position;
  }

  public Employee(String name, String department, String position) {
    super(name);
    this.department = department;
    this.position = position;
  }
}
