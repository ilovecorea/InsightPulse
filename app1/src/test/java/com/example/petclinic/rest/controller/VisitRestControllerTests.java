package com.example.petclinic.rest.controller;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.petclinic.mapper.VisitMapper;
import com.example.petclinic.mapper.VisitMapperImpl;
import com.example.petclinic.model.Owner;
import com.example.petclinic.model.Pet;
import com.example.petclinic.model.PetType;
import com.example.petclinic.model.Visit;
import com.example.petclinic.rest.advice.ExceptionControllerAdvice;
import com.example.petclinic.service.ClinicService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

@ContextConfiguration(classes = {
    ApplicationTestConfig.class,
    VisitMapperImpl.class,
})
@Import(value = {VisitRestController.class, ExceptionControllerAdvice.class})
@WebMvcTest
public class VisitRestControllerTests {

  @MockBean
  private ClinicService clinicService;

  @Autowired
  private VisitMapper visitMapper;

  @Autowired
  private MockMvc mockMvc;

  private List<Visit> visits;

  @BeforeEach
  void initVisits() {

    visits = new ArrayList<>();

    Owner owner = new Owner();
    owner.setId(1);
    owner.setFirstName("Eduardo");
    owner.setLastName("Rodriquez");
    owner.setAddress("2693 Commerce St.");
    owner.setCity("McFarland");
    owner.setTelephone("6085558763");

    PetType petType = new PetType();
    petType.setId(2);
    petType.setName("dog");

    Pet pet = new Pet();
    pet.setId(8);
    pet.setName("Rosy");
    pet.setBirthDate(LocalDate.now());
    pet.setOwner(owner);
    pet.setType(petType);


    Visit visit = new Visit();
    visit.setId(2);
    visit.setPet(pet);
    visit.setDate(LocalDate.now());
    visit.setDescription("rabies shot");
    visits.add(visit);

    visit = new Visit();
    visit.setId(3);
    visit.setPet(pet);
    visit.setDate(LocalDate.now());
    visit.setDescription("neutered");
    visits.add(visit);

  }

  @Test
  @WithMockUser(roles="OWNER_ADMIN")
  void testGetVisitSuccess() throws Exception {
    given(this.clinicService.findVisitById(2)).willReturn(visits.get(0));
    this.mockMvc.perform(get("/api/visits/2")
            .accept(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(status().isOk())
        .andExpect(content().contentType("application/json"))
        .andExpect(jsonPath("$.id").value(2))
        .andExpect(jsonPath("$.description").value("rabies shot"));
  }

  @Test
  @WithMockUser(roles="OWNER_ADMIN")
  void testGetVisitNotFound() throws Exception {
    given(this.clinicService.findVisitById(999)).willReturn(null);
    this.mockMvc.perform(get("/api/visits/999")
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound());
  }

  @Test
  @WithMockUser(roles="OWNER_ADMIN")
  void testGetAllVisitsSuccess() throws Exception {
    given(this.clinicService.findAllVisits()).willReturn(visits);
    this.mockMvc.perform(get("/api/visits/")
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType("application/json"))
        .andExpect(jsonPath("$.[0].id").value(2))
        .andExpect(jsonPath("$.[0].description").value("rabies shot"))
        .andExpect(jsonPath("$.[1].id").value(3))
        .andExpect(jsonPath("$.[1].description").value("neutered"));
  }

  @Test
  @WithMockUser(roles="OWNER_ADMIN")
  void testGetAllVisitsNotFound() throws Exception {
    visits.clear();
    given(this.clinicService.findAllVisits()).willReturn(visits);
    this.mockMvc.perform(get("/api/visits/")
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound());
  }

  @Test
  @WithMockUser(roles="OWNER_ADMIN")
  void testCreateVisitSuccess() throws Exception {
    Visit newVisit = visits.get(0);
    newVisit.setId(999);
    ObjectMapper mapper = new ObjectMapper();
    mapper.registerModule(new JavaTimeModule());
    mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    String newVisitAsJSON = mapper.writeValueAsString(visitMapper.toVisitDto(newVisit));
    System.out.println("newVisitAsJSON " + newVisitAsJSON);
    this.mockMvc.perform(post("/api/visits/")
            .content(newVisitAsJSON).accept(MediaType.APPLICATION_JSON_VALUE).contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(status().isCreated());
  }

  @Test
  @WithMockUser(roles="OWNER_ADMIN")
  void testCreateVisitError() throws Exception {
    Visit newVisit = visits.get(0);
    newVisit.setId(null);
    newVisit.setDescription(null);
    ObjectMapper mapper = new ObjectMapper();
    mapper.registerModule(new JavaTimeModule());
    String newVisitAsJSON = mapper.writeValueAsString(visitMapper.toVisitDto(newVisit));
    this.mockMvc.perform(post("/api/visits/")
            .content(newVisitAsJSON).accept(MediaType.APPLICATION_JSON_VALUE).contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(status().isBadRequest());
  }

  @Test
  @WithMockUser(roles="OWNER_ADMIN")
  void testUpdateVisitSuccess() throws Exception {
    given(this.clinicService.findVisitById(2)).willReturn(visits.get(0));
    Visit newVisit = visits.get(0);
    newVisit.setDescription("rabies shot test");
    ObjectMapper mapper = new ObjectMapper();
    mapper.registerModule(new JavaTimeModule());
    mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    String newVisitAsJSON = mapper.writeValueAsString(visitMapper.toVisitDto(newVisit));
    this.mockMvc.perform(put("/api/visits/2")
            .content(newVisitAsJSON).accept(MediaType.APPLICATION_JSON_VALUE).contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(content().contentType("application/json"))
        .andExpect(status().isNoContent());

    this.mockMvc.perform(get("/api/visits/2")
            .accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(status().isOk())
        .andExpect(content().contentType("application/json"))
        .andExpect(jsonPath("$.id").value(2))
        .andExpect(jsonPath("$.description").value("rabies shot test"));
  }

  @Test
  @WithMockUser(roles="OWNER_ADMIN")
  void testUpdateVisitError() throws Exception {
    Visit newVisit = visits.get(0);
    newVisit.setDescription(null);
    ObjectMapper mapper = new ObjectMapper();
    mapper.registerModule(new JavaTimeModule());
    String newVisitAsJSON = mapper.writeValueAsString(visitMapper.toVisitDto(newVisit));
    this.mockMvc.perform(put("/api/visits/2")
            .content(newVisitAsJSON).accept(MediaType.APPLICATION_JSON_VALUE).contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(status().isBadRequest());
  }

  @Test
  @WithMockUser(roles="OWNER_ADMIN")
  void testDeleteVisitSuccess() throws Exception {
    Visit newVisit = visits.get(0);
    ObjectMapper mapper = new ObjectMapper();
    mapper.registerModule(new JavaTimeModule());
    String newVisitAsJSON = mapper.writeValueAsString(visitMapper.toVisitDto(newVisit));
    given(this.clinicService.findVisitById(2)).willReturn(visits.get(0));
    this.mockMvc.perform(delete("/api/visits/2")
            .content(newVisitAsJSON).accept(MediaType.APPLICATION_JSON_VALUE).contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(status().isNoContent());
  }

  @Test
  @WithMockUser(roles="OWNER_ADMIN")
  void testDeleteVisitError() throws Exception {
    Visit newVisit = visits.get(0);
    ObjectMapper mapper = new ObjectMapper();
    mapper.registerModule(new JavaTimeModule());
    String newVisitAsJSON = mapper.writeValueAsString(visitMapper.toVisitDto(newVisit));
    given(this.clinicService.findVisitById(999)).willReturn(null);
    this.mockMvc.perform(delete("/api/visits/999")
            .content(newVisitAsJSON).accept(MediaType.APPLICATION_JSON_VALUE).contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(status().isNotFound());
  }
}
