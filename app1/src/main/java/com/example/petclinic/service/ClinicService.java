package com.example.petclinic.service;

import com.example.petclinic.model.Owner;
import com.example.petclinic.model.Pet;
import com.example.petclinic.model.PetType;
import com.example.petclinic.model.Specialty;
import com.example.petclinic.model.Vet;
import com.example.petclinic.model.Visit;
import com.example.petclinic.repository.OwnerRepository;
import com.example.petclinic.repository.PetRepository;
import com.example.petclinic.repository.PetTypeRepository;
import com.example.petclinic.repository.SpecialtyRepository;
import com.example.petclinic.repository.VetRepository;
import com.example.petclinic.repository.VisitRepository;
import java.util.List;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ClinicService {

  private final PetRepository petRepository;
  private final VetRepository vetRepository;
  private final OwnerRepository ownerRepository;
  private final VisitRepository visitRepository;
  private final SpecialtyRepository specialtyRepository;
  private final PetTypeRepository petTypeRepository;

  @Autowired
  public ClinicService(
      PetRepository petRepository,
      VetRepository vetRepository,
      OwnerRepository ownerRepository,
      VisitRepository visitRepository,
      SpecialtyRepository specialtyRepository,
      PetTypeRepository petTypeRepository) {
    this.petRepository = petRepository;
    this.vetRepository = vetRepository;
    this.ownerRepository = ownerRepository;
    this.visitRepository = visitRepository;
    this.specialtyRepository = specialtyRepository;
    this.petTypeRepository = petTypeRepository;
  }

  @Transactional(readOnly = true)
  public List<Pet> findAllPets() throws DataAccessException {
    return petRepository.findAll();
  }

  @Transactional
  public void deletePet(Pet pet) throws DataAccessException {
    petRepository.delete(pet);
  }

  @Transactional(readOnly = true)
  public Visit findVisitById(int visitId) throws DataAccessException {
    return visitRepository.findById(visitId)
        .orElseThrow(() -> new EmptyResultDataAccessException(1));
  }

  @Transactional(readOnly = true)
  public List<Visit> findAllVisits() throws DataAccessException {
    return visitRepository.findAll();
  }

  @Transactional
  public void deleteVisit(Visit visit) throws DataAccessException {
    visitRepository.delete(visit);
  }

  @Transactional(readOnly = true)
  public Vet findVetById(int id) throws DataAccessException {
    return vetRepository.findById(id)
        .orElseThrow(() -> new EmptyResultDataAccessException(1));
  }

  @Transactional(readOnly = true)
  public List<Vet> findAllVets() throws DataAccessException {
    return vetRepository.findAll();
  }

  @Transactional
  public void saveVet(Vet vet) throws DataAccessException {
    vetRepository.save(vet);
  }

  @Transactional
  public void deleteVet(Vet vet) throws DataAccessException {
    vetRepository.delete(vet);
  }

  @Transactional(readOnly = true)
  public List<Owner> findAllOwners() throws DataAccessException {
    return ownerRepository.findAll();
  }

  @Transactional(readOnly = true)
  public Set<Owner> findAllOwnersJoinFetch() throws DataAccessException {
    return ownerRepository.findAllJoinFetch();
  }

  @Transactional(readOnly = true)
  public List<Owner> findAllOwnersEntityGraph() throws DataAccessException {
    return ownerRepository.findAllEntityGraph();
  }

  @Transactional
  public void deleteOwner(Owner owner) throws DataAccessException {
    ownerRepository.delete(owner);
  }

  @Transactional(readOnly = true)
  public PetType findPetTypeById(int petTypeId) {
    return petTypeRepository.findById(petTypeId)
        .orElseThrow(() -> new EmptyResultDataAccessException(1));
  }

  @Transactional(readOnly = true)
  public List<PetType> findAllPetTypes() throws DataAccessException {
    return petTypeRepository.findAll();
  }

  @Transactional
  public void savePetType(PetType petType) throws DataAccessException {
    petTypeRepository.save(petType);
  }

  @Transactional
  public void deletePetType(PetType petType) throws DataAccessException {
    petTypeRepository.delete(petType);
  }

  @Transactional(readOnly = true)
  public Specialty findSpecialtyById(int specialtyId) {
    return specialtyRepository.findById(specialtyId)
        .orElseThrow(() -> new EmptyResultDataAccessException(1));
  }

  @Transactional(readOnly = true)
  public List<Specialty> findAllSpecialties() throws DataAccessException {
    return specialtyRepository.findAll();
  }

  @Transactional
  public void saveSpecialty(Specialty specialty) throws DataAccessException {
    specialtyRepository.save(specialty);
  }

  @Transactional
  public void deleteSpecialty(Specialty specialty) throws DataAccessException {
    specialtyRepository.delete(specialty);
  }

  @Transactional(readOnly = true)
  public List<PetType> findPetTypes() throws DataAccessException {
    return petRepository.findPetTypes();
  }

  @Transactional(readOnly = true)
  public Owner findOwnerById(int id) throws DataAccessException {
    return ownerRepository.findById(id)
        .orElseThrow(() -> new EmptyResultDataAccessException(1));
  }

  @Transactional(readOnly = true)
  public Pet findPetById(int id) throws DataAccessException {
    return petRepository.findById(id)
        .orElseThrow(() -> new EmptyResultDataAccessException(1));
  }

  @Transactional
  public void savePet(Pet pet) throws DataAccessException {
    petRepository.save(pet);
  }

  @Transactional
  public void saveVisit(Visit visit) throws DataAccessException {
    visitRepository.save(visit);

  }

  @Transactional(readOnly = true)
  @Cacheable(value = "vets")
  public List<Vet> findVets() throws DataAccessException {
    return vetRepository.findAll();
  }

  @Transactional
  public void saveOwner(Owner owner) throws DataAccessException {
    ownerRepository.save(owner);

  }

  @Transactional(readOnly = true)
  public List<Owner> findOwnerByLastName(String lastName) throws DataAccessException {
    return ownerRepository.findByLastName(lastName);
  }

  @Transactional(readOnly = true)
  public List<Visit> findVisitsByPetId(int petId) {
    return visitRepository.findByPetId(petId);
  }


}
