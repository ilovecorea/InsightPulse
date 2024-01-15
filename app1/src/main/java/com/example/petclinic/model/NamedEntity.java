package com.example.petclinic.model;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

/**
 * id, name 필드만 있는 상위 엔티티
 */
@MappedSuperclass
@Getter
@Setter
public class NamedEntity extends BaseEntity {

  @Column(name = "name")
  @NotEmpty
  protected String name;

  @Override
  public String toString() {
    return this.getName();
  }
}
