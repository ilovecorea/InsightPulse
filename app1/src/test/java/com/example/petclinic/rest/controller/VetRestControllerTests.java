package com.example.petclinic.rest.controller;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.petclinic.mapper.SpecialtyMapper;
import com.example.petclinic.mapper.SpecialtyMapperImpl;
import com.example.petclinic.mapper.VetMapper;
import com.example.petclinic.mapper.VetMapperImpl;
import com.example.petclinic.model.Vet;
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
    VetMapperImpl.class,
    SpecialtyMapperImpl.class
})
@Import(value = {VetRestController.class, ExceptionControllerAdvice.class})
@WebMvcTest
public class VetRestControllerTests {

  @Autowired
  private VetMapper vetMapper;

  @Autowired
  private SpecialtyMapper specialtyMapper;

  @MockBean
  private ClinicService clinicService;

  @Autowired
  private MockMvc mockMvc;

  private List<Vet> vets;

  @BeforeEach
  void initVets() {
    vets = new ArrayList<Vet>();

    Vet vet = new Vet();
    vet.setId(1);
    vet.setFirstName("James");
    vet.setLastName("Carter");
    vets.add(vet);

    vet = new Vet();
    vet.setId(2);
    vet.setFirstName("Helen");
    vet.setLastName("Leary");
    vets.add(vet);

    vet = new Vet();
    vet.setId(3);
    vet.setFirstName("Linda");
    vet.setLastName("Douglas");
    vets.add(vet);
  }

  @Test
  @WithMockUser(roles = "VET_ADMIN")
  void testGetVetSuccess() throws Exception {
    given(this.clinicService.findVetById(1)).willReturn(vets.get(0));
    this.mockMvc.perform(get("/api/vets/1")
            .accept(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(status().isOk())
        .andExpect(content().contentType("application/json"))
        .andExpect(jsonPath("$.id").value(1))
        .andExpect(jsonPath("$.firstName").value("James"));
  }

  @Test
  @WithMockUser(roles = "VET_ADMIN")
  void testGetVetNotFound() throws Exception {
    given(this.clinicService.findVetById(-1)).willReturn(null);
    this.mockMvc.perform(get("/api/vets/999")
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound());
  }

  @Test
  @WithMockUser(roles = "VET_ADMIN")
  void testGetAllVetsSuccess() throws Exception {
    given(this.clinicService.findAllVets()).willReturn(vets);
    this.mockMvc.perform(get("/api/vets/")
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType("application/json"))
        .andExpect(jsonPath("$.[0].id").value(1))
        .andExpect(jsonPath("$.[0].firstName").value("James"))
        .andExpect(jsonPath("$.[1].id").value(2))
        .andExpect(jsonPath("$.[1].firstName").value("Helen"));
  }

  @Test
  @WithMockUser(roles = "VET_ADMIN")
  void testGetAllVetsNotFound() throws Exception {
    vets.clear();
    given(this.clinicService.findAllVets()).willReturn(vets);
    this.mockMvc.perform(get("/api/vets/")
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound());
  }

  @Test
  @WithMockUser(roles = "VET_ADMIN")
  void testCreateVetSuccess() throws Exception {
    Vet newVet = vets.get(0);
    newVet.setId(999);
    ObjectMapper mapper = new ObjectMapper();
    String newVetAsJSON = mapper.writeValueAsString(vetMapper.toVetDto(newVet));
    this.mockMvc.perform(post("/api/vets/")
            .content(newVetAsJSON)
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(status().isCreated());
  }

  @Test
  @WithMockUser(roles = "VET_ADMIN")
  void testCreateVetError() throws Exception {
    Vet newVet = vets.get(0);
    newVet.setId(null);
    newVet.setFirstName(null);
    ObjectMapper mapper = new ObjectMapper();
    String newVetAsJSON = mapper.writeValueAsString(vetMapper.toVetDto(newVet));
    this.mockMvc.perform(post("/api/vets/")
            .content(newVetAsJSON).accept(MediaType.APPLICATION_JSON_VALUE)
            .contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(status().isBadRequest());
  }

  @Test
  @WithMockUser(roles = "VET_ADMIN")
  void testUpdateVetSuccess() throws Exception {
    given(this.clinicService.findVetById(1)).willReturn(vets.get(0));
    Vet newVet = vets.get(0);
    newVet.setFirstName("James");
    ObjectMapper mapper = new ObjectMapper();
    String newVetAsJSON = mapper.writeValueAsString(vetMapper.toVetDto(newVet));
    this.mockMvc.perform(put("/api/vets/1")
            .content(newVetAsJSON).accept(MediaType.APPLICATION_JSON_VALUE)
            .contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(content().contentType("application/json"))
        .andExpect(status().isNoContent());

    this.mockMvc.perform(get("/api/vets/1")
            .accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(status().isOk())
        .andExpect(content().contentType("application/json"))
        .andExpect(jsonPath("$.id").value(1))
        .andExpect(jsonPath("$.firstName").value("James"));

  }

  @Test
  @WithMockUser(roles = "VET_ADMIN")
  void testUpdateVetError() throws Exception {
    Vet newVet = vets.get(0);
    newVet.setFirstName(null);
    ObjectMapper mapper = new ObjectMapper();
    String newVetAsJSON = mapper.writeValueAsString(vetMapper.toVetDto(newVet));
    this.mockMvc.perform(put("/api/vets/1")
            .content(newVetAsJSON).accept(MediaType.APPLICATION_JSON_VALUE)
            .contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(status().isBadRequest());
  }

  @Test
  @WithMockUser(roles = "VET_ADMIN")
  void testDeleteVetSuccess() throws Exception {
    Vet newVet = vets.get(0);
    ObjectMapper mapper = new ObjectMapper();
    String newVetAsJSON = mapper.writeValueAsString(vetMapper.toVetDto(newVet));
    given(this.clinicService.findVetById(1)).willReturn(vets.get(0));
    this.mockMvc.perform(delete("/api/vets/1")
            .content(newVetAsJSON).accept(MediaType.APPLICATION_JSON_VALUE)
            .contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(status().isNoContent());
  }

  @Test
  @WithMockUser(roles = "VET_ADMIN")
  void testDeleteVetError() throws Exception {
    Vet newVet = vets.get(0);
    ObjectMapper mapper = new ObjectMapper();
    String newVetAsJSON = mapper.writeValueAsString(vetMapper.toVetDto(newVet));
    given(this.clinicService.findVetById(-1)).willReturn(null);
    this.mockMvc.perform(delete("/api/vets/999")
            .content(newVetAsJSON).accept(MediaType.APPLICATION_JSON_VALUE)
            .contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(status().isNotFound());
  }
}
