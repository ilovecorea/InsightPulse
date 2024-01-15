package com.example.petclinic.rest.controller;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.petclinic.mapper.OwnerMapper;
import com.example.petclinic.mapper.OwnerMapperImpl;
import com.example.petclinic.mapper.PetMapperImpl;
import com.example.petclinic.mapper.VisitMapper;
import com.example.petclinic.mapper.VisitMapperImpl;
import com.example.petclinic.model.Owner;
import com.example.petclinic.rest.advice.ExceptionControllerAdvice;
import com.example.petclinic.rest.dto.OwnerDto;
import com.example.petclinic.rest.dto.PetDto;
import com.example.petclinic.rest.dto.PetTypeDto;
import com.example.petclinic.rest.dto.VisitDto;
import com.example.petclinic.service.ClinicService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;


@ContextConfiguration(classes = {
    ApplicationTestConfig.class,
    OwnerMapperImpl.class,
    VisitMapperImpl.class,
    PetMapperImpl.class
})
@Import(value = {OwnerRestController.class, ExceptionControllerAdvice.class})
@WebMvcTest
class OwnerRestControllerTests {

  @Autowired
  private OwnerMapper ownerMapper;

  @Autowired
  private VisitMapper visitMapper;

  @MockBean
  private ClinicService clinicService;

  @Autowired
  private MockMvc mockMvc;

  private List<OwnerDto> owners;

  private List<PetDto> pets;

  private List<VisitDto> visits;

  @BeforeEach
  void initOwners() {
    owners = new ArrayList<>();
    OwnerDto ownerWithPet = new OwnerDto();
    owners.add(ownerWithPet.id(1)
        .firstName("George")
        .lastName("Franklin")
        .address("110 W. Liberty St.")
        .city("Madison")
        .telephone("6085551023")
        .addPetsItem(getTestPetWithIdAndName(ownerWithPet, 1, "Rosy")));
    OwnerDto owner = new OwnerDto();
    owners.add(owner.id(2)
        .firstName("Betty")
        .lastName("Davis")
        .address("638 Cardinal Ave.")
        .city("Sun Prairie")
        .telephone("6085551749"));
    owner = new OwnerDto();
    owners.add(owner.id(3)
        .firstName("Eduardo")
        .lastName("Rodriquez")
        .address("2693 Commerce St.")
        .city("McFarland")
        .telephone("6085558763"));
    owner = new OwnerDto();
    owners.add(owner.id(4)
        .firstName("Harold")
        .lastName("Davis")
        .address("563 Friendly St.")
        .city("Windsor")
        .telephone("6085553198"));

    PetTypeDto petType = new PetTypeDto();
    petType.id(2)
        .name("dog");

    pets = new ArrayList<>();
    PetDto pet = new PetDto();
    pets.add(pet.id(3)
        .name("Rosy")
        .birthDate(LocalDate.now())
        .type(petType));

    pet = new PetDto();
    pets.add(pet.id(4)
        .name("Jewel")
        .birthDate(LocalDate.now())
        .type(petType));

    visits = new ArrayList<>();
    VisitDto visit = new VisitDto();
    visit.setId(2);
    visit.setPetId(pet.getId());
    visit.setDate(LocalDate.now());
    visit.setDescription("rabies shot");
    visits.add(visit);

    visit = new VisitDto();
    visit.setId(3);
    visit.setPetId(pet.getId());
    visit.setDate(LocalDate.now());
    visit.setDescription("neutered");
    visits.add(visit);
  }

  private PetDto getTestPetWithIdAndName(final OwnerDto owner, final int id, final String name) {
    PetTypeDto petType = new PetTypeDto();
    PetDto pet = new PetDto();
    pet.id(id)
        .name(name)
        .birthDate(LocalDate.now())
        .type(petType
            .id(2)
            .name("dog"))
        .addVisitsItem(getTestVisitForPet(pet, 1));
    return pet;
  }

  private VisitDto getTestVisitForPet(final PetDto pet, final int id) {
    VisitDto visit = new VisitDto();
    return visit
        .id(id)
        .date(LocalDate.now()).description("test" + id);
  }

  @Test
  @WithMockUser(roles = "OWNER_ADMIN")
  void testGetOwnerSuccess() throws Exception {
    given(this.clinicService.findOwnerById(1)).willReturn(ownerMapper.toOwner(owners.get(0)));
    this.mockMvc.perform(get("/api/owners/1")
            .accept(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(status().isOk())
        .andExpect(content().contentType("application/json"))
        .andExpect(jsonPath("$.id").value(1))
        .andExpect(jsonPath("$.firstName").value("George"))
        .andDo(print());
  }

  @Test
  @WithMockUser(roles = "OWNER_ADMIN")
  void testGetOwnerNotFound() throws Exception {
    given(this.clinicService.findOwnerById(2)).willThrow(EmptyResultDataAccessException.class);
    this.mockMvc.perform(get("/api/owners/2")
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound());
  }

  @Test
  @WithMockUser(roles = "OWNER_ADMIN")
  void testGetOwnersListSuccess() throws Exception {
    owners.remove(0);
    owners.remove(1);
    given(this.clinicService.findOwnerByLastName("Davis")).willReturn(ownerMapper.toOwners(owners));
    this.mockMvc.perform(get("/api/owners?lastName=Davis")
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType("application/json"))
        .andExpect(jsonPath("$.[0].id").value(2))
        .andExpect(jsonPath("$.[0].firstName").value("Betty"))
        .andExpect(jsonPath("$.[1].id").value(4))
        .andExpect(jsonPath("$.[1].firstName").value("Harold"));
  }

  @Test
  @WithMockUser(roles = "OWNER_ADMIN")
  void testGetOwnersListNotFound() throws Exception {
    owners.clear();
    given(this.clinicService.findOwnerByLastName("0")).willReturn(ownerMapper.toOwners(owners));
    this.mockMvc.perform(get("/api/owners/?lastName=0")
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound());
  }

  @Test
  @WithMockUser(roles = "OWNER_ADMIN")
  void testGetAllOwnersSuccess() throws Exception {
    owners.remove(0);
    owners.remove(1);
    given(this.clinicService.findAllOwners()).willReturn(ownerMapper.toOwners(owners));
    this.mockMvc.perform(get("/api/owners/")
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType("application/json"))
        .andExpect(jsonPath("$.[0].id").value(2))
        .andExpect(jsonPath("$.[0].firstName").value("Betty"))
        .andExpect(jsonPath("$.[1].id").value(4))
        .andExpect(jsonPath("$.[1].firstName").value("Harold"))
        .andDo(print());
  }

  @Test
  @WithMockUser(roles = "OWNER_ADMIN")
  void testGetAllOwnersNotFound() throws Exception {
    owners.clear();
    given(this.clinicService.findAllOwners()).willReturn(ownerMapper.toOwners(owners));
    this.mockMvc.perform(get("/api/owners/")
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound());
  }

  @Test
  @WithMockUser(roles = "OWNER_ADMIN")
  void testCreateOwnerSuccess() throws Exception {
    OwnerDto newOwnerDto = owners.get(0);
    newOwnerDto.setId(null);
    ObjectMapper mapper = new ObjectMapper();
    mapper.registerModule(new JavaTimeModule());
    mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    String newOwnerAsJSON = mapper.writeValueAsString(newOwnerDto);
    this.mockMvc.perform(post("/api/owners/")
            .content(newOwnerAsJSON).accept(MediaType.APPLICATION_JSON_VALUE)
            .contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(status().isCreated());
  }

  @Test
  @WithMockUser(roles = "OWNER_ADMIN")
  void testCreateOwnerError() throws Exception {
    OwnerDto newOwnerDto = owners.get(0);
    newOwnerDto.setId(null);
    newOwnerDto.setFirstName(null);
    ObjectMapper mapper = new ObjectMapper();
    mapper.registerModule(new JavaTimeModule());
    mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    String newOwnerAsJSON = mapper.writeValueAsString(newOwnerDto);
    this.mockMvc.perform(post("/api/owners/")
            .content(newOwnerAsJSON).accept(MediaType.APPLICATION_JSON_VALUE)
            .contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(status().isBadRequest());
  }

  @Test
  @WithMockUser(roles = "OWNER_ADMIN")
  void testUpdateOwnerSuccess() throws Exception {
    given(this.clinicService.findOwnerById(1)).willReturn(ownerMapper.toOwner(owners.get(0)));
    int ownerId = owners.get(0).getId();
    OwnerDto updatedOwnerDto = new OwnerDto();
    // body.id = ownerId which is used in url path
    updatedOwnerDto.setId(ownerId);
    updatedOwnerDto.setFirstName("GeorgeI");
    updatedOwnerDto.setLastName("Franklin");
    updatedOwnerDto.setAddress("110 W. Liberty St.");
    updatedOwnerDto.setCity("Madison");
    updatedOwnerDto.setTelephone("6085551023");
    ObjectMapper mapper = new ObjectMapper();
    mapper.registerModule(new JavaTimeModule());
    mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    String newOwnerAsJSON = mapper.writeValueAsString(updatedOwnerDto);
    this.mockMvc.perform(put("/api/owners/" + ownerId)
            .content(newOwnerAsJSON).accept(MediaType.APPLICATION_JSON_VALUE)
            .contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(content().contentType("application/json"))
        .andExpect(status().isNoContent());

    this.mockMvc.perform(get("/api/owners/" + ownerId)
            .accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(status().isOk())
        .andExpect(content().contentType("application/json"))
        .andExpect(jsonPath("$.id").value(ownerId))
        .andExpect(jsonPath("$.firstName").value("GeorgeI"));

  }

  @Test
  @WithMockUser(roles = "OWNER_ADMIN")
  void testUpdateOwnerSuccessNoBodyId() throws Exception {
    given(this.clinicService.findOwnerById(1)).willReturn(ownerMapper.toOwner(owners.get(0)));
    int ownerId = owners.get(0).getId();
    OwnerDto updatedOwnerDto = new OwnerDto();
    updatedOwnerDto.setFirstName("GeorgeI");
    updatedOwnerDto.setLastName("Franklin");
    updatedOwnerDto.setAddress("110 W. Liberty St.");
    updatedOwnerDto.setCity("Madison");

    updatedOwnerDto.setTelephone("6085551023");
    ObjectMapper mapper = new ObjectMapper();
    String newOwnerAsJSON = mapper.writeValueAsString(updatedOwnerDto);
    this.mockMvc.perform(put("/api/owners/" + ownerId)
            .content(newOwnerAsJSON).accept(MediaType.APPLICATION_JSON_VALUE)
            .contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(content().contentType("application/json"))
        .andExpect(status().isNoContent());

    this.mockMvc.perform(get("/api/owners/" + ownerId)
            .accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(status().isOk())
        .andExpect(content().contentType("application/json"))
        .andExpect(jsonPath("$.id").value(ownerId))
        .andExpect(jsonPath("$.firstName").value("GeorgeI"));

  }

  @Test
  @WithMockUser(roles = "OWNER_ADMIN")
  void testUpdateOwnerError() throws Exception {
    OwnerDto newOwnerDto = owners.get(0);
    newOwnerDto.setFirstName("");
    ObjectMapper mapper = new ObjectMapper();
    mapper.registerModule(new JavaTimeModule());
    String newOwnerAsJSON = mapper.writeValueAsString(newOwnerDto);
    this.mockMvc.perform(put("/api/owners/1")
            .content(newOwnerAsJSON).accept(MediaType.APPLICATION_JSON_VALUE)
            .contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(status().isBadRequest());
  }

  @Test
  @WithMockUser(roles = "OWNER_ADMIN")
  void testDeleteOwnerSuccess() throws Exception {
    OwnerDto newOwnerDto = owners.get(0);
    ObjectMapper mapper = new ObjectMapper();
    mapper.registerModule(new JavaTimeModule());
    String newOwnerAsJSON = mapper.writeValueAsString(newOwnerDto);
    final Owner owner = ownerMapper.toOwner(owners.get(0));
    given(this.clinicService.findOwnerById(1)).willReturn(owner);
    this.mockMvc.perform(delete("/api/owners/1")
            .content(newOwnerAsJSON).accept(MediaType.APPLICATION_JSON_VALUE)
            .contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(status().isNoContent());
  }

  @Test
  @WithMockUser(roles = "OWNER_ADMIN")
  void testDeleteOwnerError() throws Exception {
    OwnerDto newOwnerDto = owners.get(0);
    ObjectMapper mapper = new ObjectMapper();
    mapper.registerModule(new JavaTimeModule());
    String newOwnerAsJSON = mapper.writeValueAsString(newOwnerDto);
    given(this.clinicService.findOwnerById(999)).willReturn(null);
    this.mockMvc.perform(delete("/api/owners/999")
            .content(newOwnerAsJSON).accept(MediaType.APPLICATION_JSON_VALUE)
            .contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(status().isNoContent());
  }

  @Test
  @WithMockUser(roles = "OWNER_ADMIN")
  void testCreatePetSuccess() throws Exception {
    PetDto newPet = pets.get(0);
    newPet.setId(999);
    ObjectMapper mapper = new ObjectMapper();
    mapper.registerModule(new JavaTimeModule());
    mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd"));
    mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    String newPetAsJSON = mapper.writeValueAsString(newPet);
    System.err.println("--> newPetAsJSON=" + newPetAsJSON);
    this.mockMvc.perform(post("/api/owners/1/pets/")
            .content(newPetAsJSON).accept(MediaType.APPLICATION_JSON_VALUE)
            .contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(status().isCreated());
  }

  @Test
  @WithMockUser(roles = "OWNER_ADMIN")
  void testCreatePetError() throws Exception {
    PetDto newPet = pets.get(0);
    newPet.setId(null);
    newPet.setName(null);
    ObjectMapper mapper = new ObjectMapper();
    mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd"));
    mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    mapper.registerModule(new JavaTimeModule());
    String newPetAsJSON = mapper.writeValueAsString(newPet);
    this.mockMvc.perform(post("/api/owners/1/pets/")
            .content(newPetAsJSON).accept(MediaType.APPLICATION_JSON_VALUE)
            .contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(status().isBadRequest()).andDo(print());
  }

  @Test
  @WithMockUser(roles = "OWNER_ADMIN")
  void testCreateVisitSuccess() throws Exception {
    VisitDto newVisit = visits.get(0);
    newVisit.setId(999);
    ObjectMapper mapper = new ObjectMapper();
    mapper.registerModule(new JavaTimeModule());
    mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    String newVisitAsJSON = mapper.writeValueAsString(visitMapper.toVisit(newVisit));
    System.out.println("newVisitAsJSON " + newVisitAsJSON);
    this.mockMvc.perform(post("/api/owners/1/pets/1/visits")
            .content(newVisitAsJSON).accept(MediaType.APPLICATION_JSON_VALUE)
            .contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(status().isCreated());
  }

}
