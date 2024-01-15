package com.example.petclinic.mapper;

import com.example.petclinic.model.Pet;
import com.example.petclinic.model.PetType;
import com.example.petclinic.rest.dto.PetDto;
import com.example.petclinic.rest.dto.PetFieldsDto;
import com.example.petclinic.rest.dto.PetTypeDto;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Map Pet & PetDto using mapstruct
 */
@Mapper
public interface PetMapper {

    @Mapping(source = "owner.id", target = "ownerId")
    PetDto toPetDto(Pet pet);

    List<PetDto> toPetsDto(List<Pet> pets);

    List<Pet> toPets(List<PetDto> pets);

    Pet toPet(PetDto petDto);

    Pet toPet(PetFieldsDto petFieldsDto);

    PetTypeDto toPetTypeDto(PetType petType);

    PetType toPetType(PetTypeDto petTypeDto);

    List<PetTypeDto> toPetTypeDtos(List<PetType> petTypes);
}
