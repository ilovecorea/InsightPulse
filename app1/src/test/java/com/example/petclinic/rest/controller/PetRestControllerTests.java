package com.example.petclinic.rest.controller;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.petclinic.mapper.PetMapper;
import com.example.petclinic.mapper.PetMapperImpl;
import com.example.petclinic.model.Pet;
import com.example.petclinic.rest.advice.ExceptionControllerAdvice;
import com.example.petclinic.rest.dto.OwnerDto;
import com.example.petclinic.rest.dto.PetDto;
import com.example.petclinic.rest.dto.PetTypeDto;
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
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

@ContextConfiguration(classes = {
    ApplicationTestConfig.class,
    PetMapperImpl.class
})
@Import(value = {PetRestController.class, ExceptionControllerAdvice.class})
@WebMvcTest
public class PetRestControllerTests {

  @MockBean
  protected ClinicService clinicService;

  @Autowired
  private PetMapper petMapper;

  @Autowired
  private MockMvc mockMvc;

  private List<PetDto> pets;

  @BeforeEach
  void initPets() {
    pets = new ArrayList<>();

    OwnerDto owner = new OwnerDto();
    owner.id(1).firstName("Eduardo")
        .lastName("Rodriquez")
        .address("2693 Commerce St.")
        .city("McFarland")
        .telephone("6085558763");

    PetTypeDto petType = new PetTypeDto();
    petType.id(2)
        .name("dog");

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
  }

  @Test
  @WithMockUser(roles = "OWNER_ADMIN")
  void testGetPetSuccess() throws Exception {
    given(this.clinicService.findPetById(3)).willReturn(petMapper.toPet(pets.get(0)));
    this.mockMvc.perform(get("/api/pets/3")
            .accept(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(status().isOk())
        .andExpect(content().contentType("application/json"))
        .andExpect(jsonPath("$.id").value(3))
        .andExpect(jsonPath("$.name").value("Rosy"));
  }

  @Test
  @WithMockUser(roles = "OWNER_ADMIN")
  void testGetPetNotFound() throws Exception {
    given(this.clinicService.findPetById(999)).willReturn(null);
    this.mockMvc.perform(get("/api/pets/999")
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound());
  }

  @Test
  @WithMockUser(roles = "OWNER_ADMIN")
  void testGetAllPetsSuccess() throws Exception {
    final List<Pet> pets = petMapper.toPets(this.pets);
    when(this.clinicService.findAllPets()).thenReturn(pets);
    //given(this.clinicService.findAllPets()).willReturn(petMapper.toPets(pets));
    this.mockMvc.perform(get("/api/pets/")
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType("application/json"))
        .andExpect(jsonPath("$.[0].id").value(3))
        .andExpect(jsonPath("$.[0].name").value("Rosy"))
        .andExpect(jsonPath("$.[1].id").value(4))
        .andExpect(jsonPath("$.[1].name").value("Jewel"));
  }

  @Test
  @WithMockUser(roles = "OWNER_ADMIN")
  void testGetAllPetsNotFound() throws Exception {
    pets.clear();
    given(this.clinicService.findAllPets()).willReturn(petMapper.toPets(pets));
    this.mockMvc.perform(get("/api/pets/")
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound());
  }

  @Test
  @WithMockUser(roles = "OWNER_ADMIN")
  void testUpdatePetSuccess() throws Exception {
    given(this.clinicService.findPetById(3)).willReturn(petMapper.toPet(pets.get(0)));
    PetDto newPet = pets.get(0);
    newPet.setName("Rosy I");
    ObjectMapper mapper = new ObjectMapper();
    mapper.registerModule(new JavaTimeModule());
    mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd"));
    mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    String newPetAsJSON = mapper.writeValueAsString(newPet);
    this.mockMvc.perform(put("/api/pets/3")
            .content(newPetAsJSON).accept(MediaType.APPLICATION_JSON_VALUE).contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(content().contentType("application/json"))
        .andExpect(status().isNoContent());

    this.mockMvc.perform(get("/api/pets/3")
            .accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(status().isOk())
        .andExpect(content().contentType("application/json"))
        .andExpect(jsonPath("$.id").value(3))
        .andExpect(jsonPath("$.name").value("Rosy I"));

  }

  @Test
  @WithMockUser(roles = "OWNER_ADMIN")
  void testUpdatePetError() throws Exception {
    PetDto newPet = pets.get(0);
    newPet.setName(null);
    ObjectMapper mapper = new ObjectMapper();
    mapper.registerModule(new JavaTimeModule());
    mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd"));
    mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    String newPetAsJSON = mapper.writeValueAsString(newPet);

    this.mockMvc.perform(put("/api/pets/3")
            .content(newPetAsJSON).accept(MediaType.APPLICATION_JSON_VALUE).contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(status().isBadRequest());
  }

  @Test
  @WithMockUser(roles = "OWNER_ADMIN")
  void testDeletePetSuccess() throws Exception {
    PetDto newPet = pets.get(0);
    ObjectMapper mapper = new ObjectMapper();
    mapper.registerModule(new JavaTimeModule());
    String newPetAsJSON = mapper.writeValueAsString(newPet);
    given(this.clinicService.findPetById(3)).willReturn(petMapper.toPet(pets.get(0)));
    this.mockMvc.perform(delete("/api/pets/3")
            .content(newPetAsJSON).accept(MediaType.APPLICATION_JSON_VALUE).contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(status().isNoContent());
  }

  @Test
  @WithMockUser(roles = "OWNER_ADMIN")
  void testDeletePetError() throws Exception {
    PetDto newPet = pets.get(0);
    ObjectMapper mapper = new ObjectMapper();
    mapper.registerModule(new JavaTimeModule());
    String newPetAsJSON = mapper.writeValueAsString(newPet);
    given(this.clinicService.findPetById(999)).willReturn(null);
    this.mockMvc.perform(delete("/api/pets/999")
            .content(newPetAsJSON).accept(MediaType.APPLICATION_JSON_VALUE).contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(status().isNotFound());
  }
}
