package com.example.petclinic.model;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@MappedSuperclass
@Getter
@Setter
public class Person extends BaseEntity {

  @Column(name = "first_name")
  @NotEmpty
  protected String firstName;

  @Column(name = "last_name")
  @NotEmpty
  protected String lastName;

}
