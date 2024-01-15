package com.example.petclinic.repository;

import com.example.petclinic.model.PetType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PetTypeRepository extends JpaRepository<PetType, Integer> {
	
}
