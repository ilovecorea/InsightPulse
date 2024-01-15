package com.example.petclinic.rest.controller;

import com.example.petclinic.mapper.PetTypeMapper;
import com.example.petclinic.model.PetType;
import com.example.petclinic.rest.api.PettypesApi;
import com.example.petclinic.rest.dto.PetTypeDto;
import com.example.petclinic.service.ClinicService;
import java.util.List;
import javax.transaction.Transactional;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@CrossOrigin(exposedHeaders = "errors, content-type")
@RequestMapping("api")
public class PetTypeRestController implements PettypesApi {

  private final ClinicService clinicService;
  private final PetTypeMapper petTypeMapper;

  public PetTypeRestController(ClinicService clinicService, PetTypeMapper petTypeMapper) {
    this.clinicService = clinicService;
    this.petTypeMapper = petTypeMapper;
  }

  /**
   * PetType 리스트 조회
   */
  @PreAuthorize("hasAnyRole(@roles.OWNER_ADMIN, @roles.VET_ADMIN)")
  @Override
  public ResponseEntity<List<PetTypeDto>> listPetTypes() {
    List<PetType> petTypes = this.clinicService.findAllPetTypes();
    if (petTypes.isEmpty()) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    return new ResponseEntity<>(petTypeMapper.toPetTypeDtos(petTypes), HttpStatus.OK);
  }

  /**
   * id로 PetType 조회
   */
  @PreAuthorize("hasAnyRole(@roles.OWNER_ADMIN, @roles.VET_ADMIN)")
  @Override
  public ResponseEntity<PetTypeDto> getPetType(Integer petTypeId) {
    PetType petType = this.clinicService.findPetTypeById(petTypeId);
    if (petType == null) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    return new ResponseEntity<>(petTypeMapper.toPetTypeDto(petType), HttpStatus.OK);
  }

  /**
   * PetType 추가
   */
  @PreAuthorize("hasRole(@roles.VET_ADMIN)")
  @Override
  public ResponseEntity<PetTypeDto> addPetType(PetTypeDto petTypeDto) {
    HttpHeaders headers = new HttpHeaders();
    final PetType type = petTypeMapper.toPetType(petTypeDto);
    this.clinicService.savePetType(type);
    headers.setLocation(UriComponentsBuilder.newInstance().path("/api/pettypes/{id}").buildAndExpand(type.getId()).toUri());
    return new ResponseEntity<>(petTypeMapper.toPetTypeDto(type), headers, HttpStatus.CREATED);
  }

  /**
   * PetType 수정
   */
  @PreAuthorize("hasRole(@roles.VET_ADMIN)")
  @Override
  public ResponseEntity<PetTypeDto> updatePetType(Integer petTypeId, PetTypeDto petTypeDto) {
    PetType currentPetType = this.clinicService.findPetTypeById(petTypeId);
    currentPetType.setName(petTypeDto.getName());
    this.clinicService.savePetType(currentPetType);
    return new ResponseEntity<>(petTypeMapper.toPetTypeDto(currentPetType), HttpStatus.NO_CONTENT);
  }

  /**
   * PetType 삭제
   */
  @PreAuthorize("hasRole(@roles.VET_ADMIN)")
  @Transactional
  @Override
  public ResponseEntity<PetTypeDto> deletePetType(Integer petTypeId) {
    PetType petType = this.clinicService.findPetTypeById(petTypeId);
    if (petType == null) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    this.clinicService.deletePetType(petType);
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }
}
