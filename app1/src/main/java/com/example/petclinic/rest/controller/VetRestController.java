package com.example.petclinic.rest.controller;

import com.example.petclinic.mapper.SpecialtyMapper;
import com.example.petclinic.mapper.VetMapper;
import com.example.petclinic.model.Specialty;
import com.example.petclinic.model.Vet;
import com.example.petclinic.rest.api.VetsApi;
import com.example.petclinic.rest.dto.VetDto;
import com.example.petclinic.service.ClinicService;
import java.util.ArrayList;
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
public class VetRestController implements VetsApi {

    private final ClinicService clinicService;
    private final VetMapper vetMapper;
    private final SpecialtyMapper specialtyMapper;

    public VetRestController(ClinicService clinicService, VetMapper vetMapper, SpecialtyMapper specialtyMapper) {
        this.clinicService = clinicService;
        this.vetMapper = vetMapper;
        this.specialtyMapper = specialtyMapper;
    }

    /**
     * Vet 리스트 조회
     */
    @PreAuthorize("hasRole(@roles.VET_ADMIN)")
    @Override
    public ResponseEntity<List<VetDto>> listVets() {
        List<VetDto> vets = new ArrayList<>();
        vets.addAll(vetMapper.toVetDtos(this.clinicService.findAllVets()));
        if (vets.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(vets, HttpStatus.OK);
    }

    /**
     * Vet 조회
     */
    @PreAuthorize("hasRole(@roles.VET_ADMIN)")
    @Override
    public ResponseEntity<VetDto> getVet(Integer vetId)  {
        Vet vet = this.clinicService.findVetById(vetId);
        if (vet == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(vetMapper.toVetDto(vet), HttpStatus.OK);
    }

    /**
     * Vet 추가
     */
    @PreAuthorize("hasRole(@roles.VET_ADMIN)")
    @Override
    public ResponseEntity<VetDto> addVet(VetDto vetDto) {
        HttpHeaders headers = new HttpHeaders();
        Vet vet = vetMapper.toVet(vetDto);
        this.clinicService.saveVet(vet);
        headers.setLocation(UriComponentsBuilder.newInstance().path("/api/vets/{id}").buildAndExpand(vet.getId()).toUri());
        return new ResponseEntity<>(vetMapper.toVetDto(vet), headers, HttpStatus.CREATED);
    }

    /**
     * Vet 수정
     */
    @PreAuthorize("hasRole(@roles.VET_ADMIN)")
    @Override
    public ResponseEntity<VetDto> updateVet(Integer vetId,VetDto vetDto)  {
        Vet currentVet = this.clinicService.findVetById(vetId);
        currentVet.setFirstName(vetDto.getFirstName());
        currentVet.setLastName(vetDto.getLastName());
        currentVet.getSpecialties().clear();
        for (Specialty spec : specialtyMapper.toSpecialtys(vetDto.getSpecialties())) {
            currentVet.getSpecialties().add(spec);
        }
        this.clinicService.saveVet(currentVet);
        return new ResponseEntity<>(vetMapper.toVetDto(currentVet), HttpStatus.NO_CONTENT);
    }

    /**
     * Vet 삭제
     */
    @PreAuthorize("hasRole(@roles.VET_ADMIN)")
    @Transactional
    @Override
    public ResponseEntity<VetDto> deleteVet(Integer vetId) {
        Vet vet = this.clinicService.findVetById(vetId);
        if (vet == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        this.clinicService.deleteVet(vet);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
