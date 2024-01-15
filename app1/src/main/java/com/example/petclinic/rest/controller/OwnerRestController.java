package com.example.petclinic.rest.controller;

import com.example.petclinic.mapper.OwnerMapper;
import com.example.petclinic.mapper.PetMapper;
import com.example.petclinic.mapper.VisitMapper;
import com.example.petclinic.model.Owner;
import com.example.petclinic.model.Pet;
import com.example.petclinic.model.Visit;
import com.example.petclinic.rest.api.OwnersApi;
import com.example.petclinic.rest.dto.OwnerDto;
import com.example.petclinic.rest.dto.OwnerFieldsDto;
import com.example.petclinic.rest.dto.PetDto;
import com.example.petclinic.rest.dto.PetFieldsDto;
import com.example.petclinic.rest.dto.VisitDto;
import com.example.petclinic.rest.dto.VisitFieldsDto;
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
@RequestMapping("/api")
public class OwnerRestController implements OwnersApi {

  private final ClinicService clinicService;

  private final OwnerMapper ownerMapper;

  private final PetMapper petMapper;

  private final VisitMapper visitMapper;

  public OwnerRestController(
      ClinicService clinicService,
      OwnerMapper ownerMapper,
      PetMapper petMapper,
      VisitMapper visitMapper) {
    this.clinicService = clinicService;
    this.ownerMapper = ownerMapper;
    this.petMapper = petMapper;
    this.visitMapper = visitMapper;
  }

  /**
   * Owners 조회
   */
  @PreAuthorize("hasRole(@roles.OWNER_ADMIN)")
  @Override
  public ResponseEntity<List<OwnerDto>> listOwners(String lastName) {
    List<Owner> owners;
    if (lastName != null) {
      owners = this.clinicService.findOwnerByLastName(lastName);
    } else {
      owners = this.clinicService.findAllOwners();
    }
    if (owners.isEmpty()) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    return new ResponseEntity<>(ownerMapper.toOwnerDtoCollection(owners), HttpStatus.OK);
  }

  /**
   * id로 Owner 조회
   */
  @PreAuthorize("hasRole(@roles.OWNER_ADMIN)")
  @Override
  public ResponseEntity<OwnerDto> getOwner(Integer ownerId) {
    Owner owner = this.clinicService.findOwnerById(ownerId);
    return new ResponseEntity<>(ownerMapper.toOwnerDto(owner), HttpStatus.OK);
  }

  /**
   * Owner 추가
   */
  @PreAuthorize("hasRole(@roles.OWNER_ADMIN)")
  @Override
  public ResponseEntity<OwnerDto> addOwner(OwnerFieldsDto ownerFieldsDto) {
    HttpHeaders headers = new HttpHeaders();
    Owner owner = ownerMapper.toOwner(ownerFieldsDto);
    this.clinicService.saveOwner(owner);
    OwnerDto ownerDto = ownerMapper.toOwnerDto(owner);
    headers.setLocation(UriComponentsBuilder.newInstance()
        .path("/api/owners/{id}").buildAndExpand(owner.getId()).toUri());
    return new ResponseEntity<>(ownerDto, headers, HttpStatus.CREATED);
  }

  /**
   * Owner 수정
   */
  @PreAuthorize("hasRole(@roles.OWNER_ADMIN)")
  @Override
  public ResponseEntity<OwnerDto> updateOwner(Integer ownerId, OwnerFieldsDto ownerFieldsDto) {
    Owner currentOwner = this.clinicService.findOwnerById(ownerId);
    currentOwner.setAddress(ownerFieldsDto.getAddress());
    currentOwner.setCity(ownerFieldsDto.getCity());
    currentOwner.setFirstName(ownerFieldsDto.getFirstName());
    currentOwner.setLastName(ownerFieldsDto.getLastName());
    currentOwner.setTelephone(ownerFieldsDto.getTelephone());
    this.clinicService.saveOwner(currentOwner);
    return new ResponseEntity<>(ownerMapper.toOwnerDto(currentOwner), HttpStatus.NO_CONTENT);
  }

  /**
   * Owner 삭제
   */
  @PreAuthorize("hasRole(@roles.OWNER_ADMIN)")
  @Transactional
  @Override
  public ResponseEntity<OwnerDto> deleteOwner(Integer ownerId) {
    Owner owner = this.clinicService.findOwnerById(ownerId);
    this.clinicService.deleteOwner(owner);
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  /**
   * Owner에 Pet 추가
   */
  @PreAuthorize("hasRole(@roles.OWNER_ADMIN)")
  @Override
  public ResponseEntity<PetDto> addPetToOwner(Integer ownerId, PetFieldsDto petFieldsDto) {
    HttpHeaders headers = new HttpHeaders();
    Pet pet = petMapper.toPet(petFieldsDto);
    Owner owner = new Owner();
    owner.setId(ownerId);
    pet.setOwner(owner);
    this.clinicService.savePet(pet);
    PetDto petDto = petMapper.toPetDto(pet);
    headers.setLocation(UriComponentsBuilder.newInstance().path("/api/pets/{id}")
        .buildAndExpand(pet.getId()).toUri());
    return new ResponseEntity<>(petDto, headers, HttpStatus.CREATED);
  }

  /**
   * Owner 방문 추가
   */
  @PreAuthorize("hasRole(@roles.OWNER_ADMIN)")
  @Override
  public ResponseEntity<VisitDto> addVisitToOwner(Integer ownerId, Integer petId, VisitFieldsDto visitFieldsDto) {
    HttpHeaders headers = new HttpHeaders();
    Visit visit = visitMapper.toVisit(visitFieldsDto);
    Pet pet = new Pet();
    pet.setId(petId);
    visit.setPet(pet);
    this.clinicService.saveVisit(visit);
    VisitDto visitDto = visitMapper.toVisitDto(visit);
    headers.setLocation(UriComponentsBuilder.newInstance().path("/api/visits/{id}")
        .buildAndExpand(visit.getId()).toUri());
    return new ResponseEntity<>(visitDto, headers, HttpStatus.CREATED);
  }
}
