package com.example.petclinic.model;

import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.Hibernate;

@Entity
@Getter
@Setter
@Table(name = "owners")
public class Owner extends Person {

  @Column(name = "address")
  @NotEmpty
  private String address;

  @Column(name = "city")
  @NotEmpty
  private String city;

  @Column(name = "telephone")
  @NotEmpty
  @Digits(fraction = 0, integer = 10)
  private String telephone;

  /**
   * 주인은 여러 마리의 동물을 가짐
   * cascadeType:
   * - persist: 저장시 자식도 저장
   * - merge: 병합시 자식도 병합
   * - remove: 삭제시 자식도 삭제
   * - refresh: 새로 고침시 자식도 새로 고침
   * fatchType:
   * - eager: 연관된 엔티티를 즉시 로딩
   * - lazy: 연관된 엔티티를 지연 로딩
   */
//  @BatchSize(size = 1000)
//  @OneToMany(cascade = CascadeType.ALL, mappedBy = "owner", fetch = FetchType.EAGER)
  @OneToMany(cascade = CascadeType.ALL, mappedBy = "owner")
  private Set<Pet> pets = new LinkedHashSet<>();

  @Override
  public String toString() {
    return getClass().getSimpleName() + "(" +
        "id = " + id + ", " +
        "address = " + address + ", " +
        "city = " + city + ", " +
        "telephone = " + telephone + ", " +
        "firstName = " + firstName + ", " +
        "lastName = " + lastName + ")";
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) {
      return false;
    }
    Owner owner = (Owner) o;
    return id != null && Objects.equals(id, owner.id);
  }

  @Override
  public int hashCode() {
    return getClass().hashCode();
  }
}
