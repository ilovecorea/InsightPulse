package com.example.petclinic.rest.controller;

import com.example.petclinic.mapper.VisitMapper;
import com.example.petclinic.model.Visit;
import com.example.petclinic.rest.api.VisitsApi;
import com.example.petclinic.rest.dto.VisitDto;
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
public class VisitRestController implements VisitsApi {

    private final ClinicService clinicService;

    private final VisitMapper visitMapper;

    public VisitRestController(ClinicService clinicService, VisitMapper visitMapper) {
        this.clinicService = clinicService;
        this.visitMapper = visitMapper;
    }

    /**
     * Vet 리스트 조회
     */
    @PreAuthorize("hasRole(@roles.OWNER_ADMIN)")
    @Override
    public ResponseEntity<List<VisitDto>> listVisits() {
        List<Visit> visits = this.clinicService.findAllVisits();
        if (visits.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(visitMapper.toVisitsDto(visits), HttpStatus.OK);
    }

    /**
     * Vet 리스트 조회
     */
    @PreAuthorize("hasRole(@roles.OWNER_ADMIN)")
    @Override
    public ResponseEntity<VisitDto> getVisit( Integer visitId) {
        Visit visit = this.clinicService.findVisitById(visitId);
        if (visit == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(visitMapper.toVisitDto(visit), HttpStatus.OK);
    }

    /**
     * Visit 추가
     */
    @PreAuthorize("hasRole(@roles.OWNER_ADMIN)")
    @Override
    public ResponseEntity<VisitDto> addVisit(VisitDto visitDto) {
        HttpHeaders headers = new HttpHeaders();
        Visit visit = visitMapper.toVisit(visitDto);
        this.clinicService.saveVisit(visit);
        visitDto = visitMapper.toVisitDto(visit);
        headers.setLocation(UriComponentsBuilder.newInstance().path("/api/visits/{id}").buildAndExpand(visit.getId()).toUri());
        return new ResponseEntity<>(visitDto, headers, HttpStatus.CREATED);
    }

    /**
     * Visit 수정
     */
    @PreAuthorize("hasRole(@roles.OWNER_ADMIN)")
    @Override
    public ResponseEntity<VisitDto> updateVisit(Integer visitId, VisitDto visitDto) {
        Visit currentVisit = this.clinicService.findVisitById(visitId);
        currentVisit.setDate(visitDto.getDate());
        currentVisit.setDescription(visitDto.getDescription());
        this.clinicService.saveVisit(currentVisit);
        return new ResponseEntity<>(visitMapper.toVisitDto(currentVisit), HttpStatus.NO_CONTENT);
    }

    /**
     * Visit 삭제
     */
    @PreAuthorize("hasRole(@roles.OWNER_ADMIN)")
    @Transactional
    @Override
    public ResponseEntity<VisitDto> deleteVisit(Integer visitId) {
        Visit visit = this.clinicService.findVisitById(visitId);
        if (visit == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        this.clinicService.deleteVisit(visit);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
