package com.example.petclinic.rest.controller;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.petclinic.mapper.PetTypeMapper;
import com.example.petclinic.mapper.PetTypeMapperImpl;
import com.example.petclinic.model.PetType;
import com.example.petclinic.rest.advice.ExceptionControllerAdvice;
import com.example.petclinic.service.ClinicService;
import com.fasterxml.jackson.databind.ObjectMapper;
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
    PetTypeMapperImpl.class
})
@Import(value = {PetTypeRestController.class, ExceptionControllerAdvice.class})
@WebMvcTest
public class PetTypeControllerTests {

  @Autowired
  private PetTypeMapper petTypeMapper;

  @MockBean
  private ClinicService clinicService;

  @Autowired
  private MockMvc mockMvc;

  private List<PetType> petTypes;

  @BeforeEach
  void initPetTypes(){
    petTypes = new ArrayList<PetType>();

    PetType petType = new PetType();
    petType.setId(1);
    petType.setName("cat");
    petTypes.add(petType);

    petType = new PetType();
    petType.setId(2);
    petType.setName("dog");
    petTypes.add(petType);

    petType = new PetType();
    petType.setId(3);
    petType.setName("lizard");
    petTypes.add(petType);

    petType = new PetType();
    petType.setId(4);
    petType.setName("snake");
    petTypes.add(petType);
  }

  @Test
  @WithMockUser(roles="OWNER_ADMIN")
  void testGetPetTypeSuccessAsOwnerAdmin() throws Exception {
    given(this.clinicService.findPetTypeById(1)).willReturn(petTypes.get(0));
    this.mockMvc.perform(get("/api/pettypes/1")
            .accept(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(status().isOk())
        .andExpect(content().contentType("application/json"))
        .andExpect(jsonPath("$.id").value(1))
        .andExpect(jsonPath("$.name").value("cat"));
  }

  @Test
  @WithMockUser(roles="VET_ADMIN")
  void testGetPetTypeSuccessAsVetAdmin() throws Exception {
    given(this.clinicService.findPetTypeById(1)).willReturn(petTypes.get(0));
    this.mockMvc.perform(get("/api/pettypes/1")
            .accept(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(status().isOk())
        .andExpect(content().contentType("application/json"))
        .andExpect(jsonPath("$.id").value(1))
        .andExpect(jsonPath("$.name").value("cat"));
  }

  @Test
  @WithMockUser(roles="OWNER_ADMIN")
  void testGetPetTypeNotFound() throws Exception {
    given(this.clinicService.findPetTypeById(999)).willReturn(null);
    this.mockMvc.perform(get("/api/pettypes/999")
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound());
  }

  @Test
  @WithMockUser(roles="OWNER_ADMIN")
  void testGetAllPetTypesSuccessAsOwnerAdmin() throws Exception {
    petTypes.remove(0);
    petTypes.remove(1);
    given(this.clinicService.findAllPetTypes()).willReturn(petTypes);
    this.mockMvc.perform(get("/api/pettypes/")
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType("application/json"))
        .andExpect(jsonPath("$.[0].id").value(2))
        .andExpect(jsonPath("$.[0].name").value("dog"))
        .andExpect(jsonPath("$.[1].id").value(4))
        .andExpect(jsonPath("$.[1].name").value("snake"));
  }

  @Test
  @WithMockUser(roles="VET_ADMIN")
  void testGetAllPetTypesSuccessAsVetAdmin() throws Exception {
    petTypes.remove(0);
    petTypes.remove(1);
    given(this.clinicService.findAllPetTypes()).willReturn(petTypes);
    this.mockMvc.perform(get("/api/pettypes/")
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType("application/json"))
        .andExpect(jsonPath("$.[0].id").value(2))
        .andExpect(jsonPath("$.[0].name").value("dog"))
        .andExpect(jsonPath("$.[1].id").value(4))
        .andExpect(jsonPath("$.[1].name").value("snake"));
  }

  @Test
  @WithMockUser(roles="VET_ADMIN")
  void testGetAllPetTypesNotFound() throws Exception {
    petTypes.clear();
    given(this.clinicService.findAllPetTypes()).willReturn(petTypes);
    this.mockMvc.perform(get("/api/pettypes/")
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound());
  }

  @Test
  @WithMockUser(roles="VET_ADMIN")
  void testCreatePetTypeSuccess() throws Exception {
    PetType newPetType = petTypes.get(0);
    newPetType.setId(999);
    ObjectMapper mapper = new ObjectMapper();
    String newPetTypeAsJSON = mapper.writeValueAsString(petTypeMapper.toPetTypeDto(newPetType));
    this.mockMvc.perform(post("/api/pettypes/")
            .content(newPetTypeAsJSON).accept(MediaType.APPLICATION_JSON_VALUE).contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(status().isCreated());
  }

  @Test
  @WithMockUser(roles="VET_ADMIN")
  void testCreatePetTypeError() throws Exception {
    PetType newPetType = petTypes.get(0);
    newPetType.setId(null);
    newPetType.setName(null);
    ObjectMapper mapper = new ObjectMapper();
    String newPetTypeAsJSON = mapper.writeValueAsString(petTypeMapper.toPetTypeDto(newPetType));
    this.mockMvc.perform(post("/api/pettypes/")
            .content(newPetTypeAsJSON).accept(MediaType.APPLICATION_JSON_VALUE).contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(status().isBadRequest());
  }

  @Test
  @WithMockUser(roles="VET_ADMIN")
  void testUpdatePetTypeSuccess() throws Exception {
    given(this.clinicService.findPetTypeById(2)).willReturn(petTypes.get(1));
    PetType newPetType = petTypes.get(1);
    newPetType.setName("dog I");
    ObjectMapper mapper = new ObjectMapper();
    String newPetTypeAsJSON = mapper.writeValueAsString(petTypeMapper.toPetTypeDto(newPetType));
    this.mockMvc.perform(put("/api/pettypes/2")
            .content(newPetTypeAsJSON).accept(MediaType.APPLICATION_JSON_VALUE).contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(content().contentType("application/json"))
        .andExpect(status().isNoContent());

    this.mockMvc.perform(get("/api/pettypes/2")
            .accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(status().isOk())
        .andExpect(content().contentType("application/json"))
        .andExpect(jsonPath("$.id").value(2))
        .andExpect(jsonPath("$.name").value("dog I"));
  }

  @Test
  @WithMockUser(roles="VET_ADMIN")
  void testUpdatePetTypeError() throws Exception {
    PetType newPetType = petTypes.get(0);
    newPetType.setName("");
    ObjectMapper mapper = new ObjectMapper();
    String newPetTypeAsJSON = mapper.writeValueAsString(petTypeMapper.toPetTypeDto(newPetType));
    this.mockMvc.perform(put("/api/pettypes/1")
            .content(newPetTypeAsJSON).accept(MediaType.APPLICATION_JSON_VALUE).contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(status().isBadRequest());
  }

  @Test
  @WithMockUser(roles="VET_ADMIN")
  void testDeletePetTypeSuccess() throws Exception {
    PetType newPetType = petTypes.get(0);
    ObjectMapper mapper = new ObjectMapper();
    String newPetTypeAsJSON = mapper.writeValueAsString(newPetType);
    given(this.clinicService.findPetTypeById(1)).willReturn(petTypes.get(0));
    this.mockMvc.perform(delete("/api/pettypes/1")
            .content(newPetTypeAsJSON).accept(MediaType.APPLICATION_JSON_VALUE).contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(status().isNoContent());
  }

  @Test
  @WithMockUser(roles="VET_ADMIN")
  void testDeletePetTypeError() throws Exception {
    PetType newPetType = petTypes.get(0);
    ObjectMapper mapper = new ObjectMapper();
    String newPetTypeAsJSON = mapper.writeValueAsString(petTypeMapper.toPetTypeDto(newPetType));
    given(this.clinicService.findPetTypeById(999)).willReturn(null);
    this.mockMvc.perform(delete("/api/pettypes/999")
            .content(newPetTypeAsJSON).accept(MediaType.APPLICATION_JSON_VALUE).contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(status().isNotFound());
  }
}
