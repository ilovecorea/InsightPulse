package com.example.petclinic.repository;

import com.example.petclinic.model.Visit;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VisitRepository extends JpaRepository<Visit, Integer> {

  List<Visit> findByPetId(int petId);
}
