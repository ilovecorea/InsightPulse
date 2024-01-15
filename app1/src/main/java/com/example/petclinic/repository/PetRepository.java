package com.example.petclinic.repository;

import com.example.petclinic.model.Pet;
import com.example.petclinic.model.PetType;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PetRepository extends JpaRepository<Pet, Integer> {

  @Query("SELECT ptype FROM PetType ptype ORDER BY ptype.name")
  List<PetType> findPetTypes();
}
