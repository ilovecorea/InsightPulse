package com.example.petclinic.service;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.example.petclinic.model.Owner;
import com.example.petclinic.model.Pet;
import com.example.petclinic.model.PetType;
import com.example.petclinic.model.Specialty;
import com.example.petclinic.model.Vet;
import com.example.petclinic.model.Visit;
import com.example.petclinic.util.EntityUtils;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import org.hibernate.LazyInitializationException;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@ActiveProfiles({"local"})
public class ClinicServiceTests extends BaseServiceTests {

  @Autowired
  protected ClinicService clinicService;

  private static final Logger log = LoggerFactory.getLogger(ClinicServiceTests.class);

  @Test
  void shouldFindOwnersByLastName() {
    Collection<Owner> owners = this.clinicService.findOwnerByLastName("Davis");
    assertThat(owners.size(), is(2));

    owners = this.clinicService.findOwnerByLastName("Daviss");
    assertThat(owners.isEmpty(), is(true));
  }

  /**
   * LazyInitializationException 발생
   */
  @Test
  void shouldErrorFindSingleOwnerWithPet() {
    Owner owner = this.clinicService.findOwnerById(1);
    assertThat(owner.getLastName(), startsWith("Franklin"));
    assertThrows(LazyInitializationException.class, () -> {
      assertThat(owner.getPets().size(), is(1));
    });
//    assertThat(owner.getPets().size(), is(1));
//    List<Pet> pets = owner.getPets().stream().toList();
//    assertThat(pets.get(0).getType(), notNullValue());
//    assertThat(pets.get(0).getType().getName(), equalTo("cat"));
  }

  /**
   * no session 발생
   */
  @Test
//  @Transactional
  void shouldFindSingleOwnerWithPetFailed() {
    assertThrows(LazyInitializationException.class, () -> {
      Owner owner = this.clinicService.findOwnerById(10);
      assertThat(owner.getLastName(), startsWith("Estaban"));
      assertThat(owner.getPets().size(), is(2));
      List<Pet> pets = owner.getPets().stream().toList();
      assertThat(pets.get(0).getType(), notNullValue());
      assertThat(pets.get(0).getType().getName(), equalTo("cat"));
      assertThat(pets.get(1).getType(), notNullValue());
      assertThat(pets.get(1).getType().getName(), equalTo("dog"));
    });
  }

  @Test
  @Transactional
  void shouldFindSingleOwnerWithPet() {
    Owner owner = this.clinicService.findOwnerById(10);
    assertThat(owner.getLastName(), startsWith("Estaban"));
    assertThat(owner.getPets().size(), is(2));
    List<Pet> pets = owner.getPets().stream().toList();
    assertThat(pets.get(0).getType(), notNullValue());
    assertThat(pets.get(0).getType().getName(), equalTo("cat"));
    assertThat(pets.get(1).getType(), notNullValue());
    assertThat(pets.get(1).getType().getName(), equalTo("dog"));
  }

  /**
   * n + 1 발생
   */
  @Test
  @Transactional
  void shouldFindAllOwnersWithPets() {
    List<Owner> owners = this.clinicService.findAllOwners();
    assertThat(owners.size(), is(10));
    owners.forEach(owner -> {
      Set<Pet> pets = owner.getPets();
      assertThat(pets.size(), greaterThanOrEqualTo(1));
    });
  }

  /**
   * join fetch 사용으로 n + 1 피할수 있음 owner를 List로 받으면 pet의 수만큼 owner 증가됨
   */
  @Test
  @Transactional
  void shouldFindAllOwnersJoinFetch() {
    Set<Owner> owners = this.clinicService.findAllOwnersJoinFetch();
    assertThat(owners.size(), is(10));
    owners.forEach(owner -> {
      Set<Pet> pets = owner.getPets();
      assertThat(pets.size(), greaterThanOrEqualTo(1));
    });
  }

  /**
   * entityGraph 사용으로 n + 1 피할수 있음
   */
  @Test
  @Transactional
  void shouldFindAllOwnersEntityGraph() {
    List<Owner> owners = this.clinicService.findAllOwnersEntityGraph();
    assertThat(owners.size(), is(10));
    owners.forEach(owner -> {
      Set<Pet> pets = owner.getPets();
      assertThat(pets.size(), greaterThanOrEqualTo(1));
    });
  }

  @Test
  @Transactional
  void shouldInsertOwner() {
    Collection<Owner> owners = this.clinicService.findOwnerByLastName("Schultz");
    int found = owners.size();

    Owner owner = new Owner();
    owner.setFirstName("Sam");
    owner.setLastName("Schultz");
    owner.setAddress("4, Evans Street");
    owner.setCity("Wollongong");
    owner.setTelephone("4444444444");
    this.clinicService.saveOwner(owner);
    assertThat(owner.getId().longValue(), not(0));
    assertThat(owner.getPets().isEmpty(), is(true));
    owners = this.clinicService.findOwnerByLastName("Schultz");
    assertThat(owners.size(), is(found + 1));
  }

  @Test
  @Transactional
  void shouldUpdateOwner() {
    Owner owner = this.clinicService.findOwnerById(1);
    String oldLastName = owner.getLastName();
    String newLastName = oldLastName + "X";

    owner.setLastName(newLastName);
    this.clinicService.saveOwner(owner);

    // retrieving new name from database
    owner = this.clinicService.findOwnerById(1);
    assertThat(owner.getLastName(), equalTo(newLastName));
  }

  @Test
  void shouldFindPetWithCorrectId() {
    Pet pet7 = this.clinicService.findPetById(7);
    assertThat(pet7.getName(), startsWith("Samantha"));
    assertThat(pet7.getOwner().getFirstName(), equalTo("Jean"));
  }

  @Test
  @Transactional
  void shouldInsertPetIntoDatabaseAndGenerateId() {
    Owner owner6 = this.clinicService.findOwnerById(6);
    int found = owner6.getPets().size();

    Pet pet = new Pet();
    pet.setName("bowser");
    Collection<PetType> types = this.clinicService.findPetTypes();
    pet.setType(EntityUtils.getById(types, PetType.class, 2));
    pet.setBirthDate(LocalDate.now());
    owner6.getPets().add(pet);
    assertThat(owner6.getPets().size(), is(found + 1));

    pet.setOwner(owner6);
    this.clinicService.savePet(pet);
    this.clinicService.saveOwner(owner6);

    owner6 = this.clinicService.findOwnerById(6);
    assertThat(owner6.getPets().size(), is(found + 1));
    // checks that id has been generated
    assertThat(pet.getId(), notNullValue());
  }

  @Test
  @Transactional
  void shouldUpdatePetName() throws Exception {
    Pet pet7 = this.clinicService.findPetById(7);
    String oldName = pet7.getName();

    String newName = oldName + "X";
    pet7.setName(newName);
    this.clinicService.savePet(pet7);

    pet7 = this.clinicService.findPetById(7);
    assertThat(pet7.getName(), equalTo(newName));
  }

  @Test
  void shouldFindVets() {
    Collection<Vet> vets = this.clinicService.findVets();

    Vet vet = EntityUtils.getById(vets, Vet.class, 3);
    assertThat(vet.getLastName(), equalTo("Douglas"));
    List<Specialty> specialties = vet.getSpecialties().stream().toList();
    assertThat(specialties.size(), is(2));
    assertThat(specialties.get(0).getName(), equalTo("dentistry"));
    assertThat(specialties.get(1).getName(), equalTo("surgery"));
  }

  @Test
  @Transactional
  void shouldAddNewVisitForPet() {
    Pet pet7 = this.clinicService.findPetById(7);
    int found = pet7.getVisits().size();
    Visit visit = new Visit();
    pet7.getVisits().add(visit);
    visit.setDescription("test");
    visit.setPet(pet7);
    this.clinicService.saveVisit(visit);
    this.clinicService.savePet(pet7);

    pet7 = this.clinicService.findPetById(7);
    assertThat(pet7.getVisits().size(), is(found + 1));
    assertThat(visit.getId(), notNullValue());
  }

  @Test
  void shouldFindVisitsByPetId() throws Exception {
    Collection<Visit> visits = this.clinicService.findVisitsByPetId(7);
    assertThat(visits.size(), is(2));
    Visit[] visitArr = visits.toArray(new Visit[visits.size()]);
    assertThat(visitArr[0].getPet(), notNullValue());
    assertThat(visitArr[0].getDate(), notNullValue());
    assertThat(visitArr[0].getPet().getId(), is(7));
  }

  @Test
  void shouldFindAllPets() {
    Collection<Pet> pets = this.clinicService.findAllPets();
    Pet pet1 = EntityUtils.getById(pets, Pet.class, 1);
    assertThat(pet1.getName(), equalTo("Leo"));
    Pet pet3 = EntityUtils.getById(pets, Pet.class, 3);
    assertThat(pet3.getName(), equalTo("Rosy"));
  }

  @Test
  @Transactional
  void shouldDeletePet() {
    Pet pet = this.clinicService.findPetById(1);
    this.clinicService.deletePet(pet);
    try {
      pet = this.clinicService.findPetById(1);
    } catch (Exception e) {
      pet = null;
    }
    assertThat(pet, nullValue());
  }

  @Test
  void shouldFindVisitDyId() {
    Visit visit = this.clinicService.findVisitById(1);
    assertThat(visit.getId(), is(1));
    assertThat(visit.getPet().getName(), equalTo("Samantha"));
  }

  @Test
  void shouldFindAllVisits() {
    Collection<Visit> visits = this.clinicService.findAllVisits();
    Visit visit1 = EntityUtils.getById(visits, Visit.class, 1);
    assertThat(visit1.getPet().getName(), equalTo("Samantha"));
    Visit visit3 = EntityUtils.getById(visits, Visit.class, 3);
    assertThat(visit3.getPet().getName(), equalTo("Max"));
  }

  @Test
  @Transactional
  void shouldInsertVisit() {
    Collection<Visit> visits = this.clinicService.findAllVisits();
    int found = visits.size();

    Pet pet = this.clinicService.findPetById(1);

    Visit visit = new Visit();
    visit.setPet(pet);
    visit.setDate(LocalDate.now());
    visit.setDescription("new visit");

    this.clinicService.saveVisit(visit);
    assertThat(visit.getId().longValue(), not(0));

    visits = this.clinicService.findAllVisits();
    assertThat(visits.size(), is(found + 1));
  }

  @Test
  @Transactional
  void shouldUpdateVisit() {
    Visit visit = this.clinicService.findVisitById(1);
    String oldDesc = visit.getDescription();
    String newDesc = oldDesc + "X";
    visit.setDescription(newDesc);
    this.clinicService.saveVisit(visit);
    visit = this.clinicService.findVisitById(1);
    assertThat(visit.getDescription(), equalTo(newDesc));
  }

  @Test
  @Transactional
  void shouldDeleteVisit() {
    Visit visit = this.clinicService.findVisitById(1);
    this.clinicService.deleteVisit(visit);
    try {
      visit = this.clinicService.findVisitById(1);
    } catch (Exception e) {
      visit = null;
    }
    assertThat(visit, nullValue());
  }

  @Test
  void shouldFindVetDyId() {
    Vet vet = this.clinicService.findVetById(1);
    assertThat(vet.getFirstName(), equalTo("James"));
    assertThat(vet.getLastName(), equalTo("Carter"));
  }

  @Test
  @Transactional
  void shouldInsertVet() {
    Collection<Vet> vets = this.clinicService.findAllVets();
    int found = vets.size();

    Vet vet = new Vet();
    vet.setFirstName("John");
    vet.setLastName("Dow");

    this.clinicService.saveVet(vet);
    assertThat(vet.getId().longValue(), not(0));

    vets = this.clinicService.findAllVets();
    assertThat(vets.size(), is(found + 1));
  }

  @Test
  @Transactional
  void shouldUpdateVet() {
    Vet vet = this.clinicService.findVetById(1);
    String oldLastName = vet.getLastName();
    String newLastName = oldLastName + "X";
    vet.setLastName(newLastName);
    this.clinicService.saveVet(vet);
    vet = this.clinicService.findVetById(1);
    assertThat(vet.getLastName(), equalTo(newLastName));
  }

  @Test
  @Transactional
  void shouldDeleteVet() {
    Vet vet = this.clinicService.findVetById(1);
    this.clinicService.deleteVet(vet);
    try {
      vet = this.clinicService.findVetById(1);
    } catch (Exception e) {
      vet = null;
    }
    assertThat(vet, nullValue());
  }

  @Test
  void shouldFindAllOwners() {
    Collection<Owner> owners = this.clinicService.findAllOwners();
    Owner owner1 = EntityUtils.getById(owners, Owner.class, 1);
    assertThat(owner1.getFirstName(), equalTo("George"));
    Owner owner3 = EntityUtils.getById(owners, Owner.class, 3);
    assertThat(owner3.getFirstName(), equalTo("Eduardo"));
  }

  @Test
  @Transactional
  void shouldDeleteOwner() {
    Owner owner = this.clinicService.findOwnerById(1);
    this.clinicService.deleteOwner(owner);
    try {
      owner = this.clinicService.findOwnerById(1);
    } catch (Exception e) {
      owner = null;
    }
    assertThat(owner, nullValue());
  }

  @Test
  void shouldFindPetTypeById() {
    PetType petType = this.clinicService.findPetTypeById(1);
    assertThat(petType.getName(), equalTo("cat"));
  }

  @Test
  void shouldFindAllPetTypes() {
    Collection<PetType> petTypes = this.clinicService.findAllPetTypes();
    PetType petType1 = EntityUtils.getById(petTypes, PetType.class, 1);
    assertThat(petType1.getName(), equalTo("cat"));
    PetType petType3 = EntityUtils.getById(petTypes, PetType.class, 3);
    assertThat(petType3.getName(), equalTo("lizard"));
  }

  @Test
  @Transactional
  void shouldInsertPetType() {
    Collection<PetType> petTypes = this.clinicService.findAllPetTypes();
    int found = petTypes.size();

    PetType petType = new PetType();
    petType.setName("tiger");

    this.clinicService.savePetType(petType);
    assertThat(petType.getId().longValue(), not(0));

    petTypes = this.clinicService.findAllPetTypes();
    assertThat(petTypes.size(), is(found + 1));
  }

  @Test
  @Transactional
  void shouldUpdatePetType() {
    PetType petType = this.clinicService.findPetTypeById(1);
    String oldLastName = petType.getName();
    String newLastName = oldLastName + "X";
    petType.setName(newLastName);
    this.clinicService.savePetType(petType);
    petType = this.clinicService.findPetTypeById(1);
    assertThat(petType.getName(), equalTo(newLastName));
  }

  @Test
  @Transactional
  void shouldDeletePetType() {
    PetType petType = this.clinicService.findPetTypeById(1);
    this.clinicService.deletePetType(petType);
    try {
      petType = this.clinicService.findPetTypeById(1);
    } catch (Exception e) {
      petType = null;
    }
    assertThat(petType, nullValue());
  }

  @Test
  void shouldFindSpecialtyById() {
    Specialty specialty = this.clinicService.findSpecialtyById(1);
    assertThat(specialty.getName(), equalTo("radiology"));
  }

  @Test
  void shouldFindAllSpecialtys() {
    Collection<Specialty> specialties = this.clinicService.findAllSpecialties();
    Specialty specialty1 = EntityUtils.getById(specialties, Specialty.class, 1);
    assertThat(specialty1.getName(), equalTo("radiology"));
    Specialty specialty3 = EntityUtils.getById(specialties, Specialty.class, 3);
    assertThat(specialty3.getName(), equalTo("dentistry"));
  }

  @Test
  @Transactional
  void shouldInsertSpecialty() {
    Collection<Specialty> specialties = this.clinicService.findAllSpecialties();
    int found = specialties.size();

    Specialty specialty = new Specialty();
    specialty.setName("dermatologist");

    this.clinicService.saveSpecialty(specialty);
    assertThat(specialty.getId().longValue(), not(0));

    specialties = this.clinicService.findAllSpecialties();
    assertThat(specialties.size(), is(found + 1));
  }

  @Test
  @Transactional
  void shouldUpdateSpecialty() {
    Specialty specialty = this.clinicService.findSpecialtyById(1);
    String oldLastName = specialty.getName();
    String newLastName = oldLastName + "X";
    specialty.setName(newLastName);
    this.clinicService.saveSpecialty(specialty);
    specialty = this.clinicService.findSpecialtyById(1);
    assertThat(specialty.getName(), equalTo(newLastName));
  }

  @Test
  @Transactional
  void shouldDeleteSpecialty() {
    Specialty specialty = new Specialty();
    specialty.setName("test");
    this.clinicService.saveSpecialty(specialty);
    Integer specialtyId = specialty.getId();
    assertThat(specialtyId, notNullValue());
    specialty = this.clinicService.findSpecialtyById(specialtyId);
    assertThat(specialty, notNullValue());
    this.clinicService.deleteSpecialty(specialty);
    try {
      specialty = this.clinicService.findSpecialtyById(specialtyId);
    } catch (Exception e) {
      specialty = null;
    }
    assertThat(specialty, nullValue());
  }
}
