package com.example.petclinic.model;

import java.util.Objects;
import javax.persistence.Entity;
import javax.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.Hibernate;

@Getter
@Setter
@Entity
@Table(name = "specialties")
public class Specialty extends NamedEntity {

  @Override
  public String toString() {
    return getClass().getSimpleName() + "(" +
        "id = " + id + ", " +
        "name = " + name + ")";
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) {
      return false;
    }
    Specialty specialty = (Specialty) o;
    return id != null && Objects.equals(id, specialty.id);
  }

  @Override
  public int hashCode() {
    return getClass().hashCode();
  }
}
