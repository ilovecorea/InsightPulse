package com.example.petclinic.model;

import java.time.LocalDate;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.Hibernate;

@Getter
@Setter
@Entity
@Table(name = "visits")
public class Visit extends BaseEntity {


    /**
     * Holds value of property date.
     */
    @Column(name = "visit_date", columnDefinition = "DATE")
    private LocalDate date = LocalDate.now();

    /**
     * Holds value of property description.
     */
    @NotEmpty
    @Column(name = "description")
    private String description;

    /**
     * Holds value of property pet.
     */
    @ManyToOne
    @JoinColumn(name = "pet_id")
    private Pet pet;

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" +
            "id = " + id + ", " +
            "date = " + date + ", " +
            "description = " + description + ", " +
            "pet = " + pet + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) {
            return false;
        }
        Visit visit = (Visit) o;
        return id != null && Objects.equals(id, visit.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
