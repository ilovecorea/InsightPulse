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
@Table(name = "types")
public class PetType extends NamedEntity {

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
    PetType petType = (PetType) o;
    return id != null && Objects.equals(id, petType.id);
  }

  @Override
  public int hashCode() {
    return getClass().hashCode();
  }
}
