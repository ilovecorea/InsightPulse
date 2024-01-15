package com.example.petclinic.repository;

import com.example.petclinic.model.Owner;
import java.util.List;
import java.util.Set;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface OwnerRepository extends JpaRepository<Owner, Integer> {

  @Query("SELECT DISTINCT owner FROM Owner owner left join fetch owner.pets WHERE owner.lastName LIKE :lastName%")
  List<Owner> findByLastName(@Param("lastName") String lastName);


  @Query("SELECT o FROM Owner o JOIN FETCH o.pets")
  Set<Owner> findAllJoinFetch();

  @EntityGraph(attributePaths = "pets")
  @Query("SELECT o FROM Owner o")
  List<Owner> findAllEntityGraph();

}
