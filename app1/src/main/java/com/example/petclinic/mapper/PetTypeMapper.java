package com.example.petclinic.mapper;

import com.example.petclinic.model.PetType;
import com.example.petclinic.rest.dto.PetTypeDto;
import com.example.petclinic.rest.dto.PetTypeFieldsDto;
import java.util.List;
import org.mapstruct.Mapper;

/**
 * Map PetType & PetTypeDto using mapstruct
 */
@Mapper
public interface PetTypeMapper {

    PetType toPetType(PetTypeDto petTypeDto);

    PetType toPetType(PetTypeFieldsDto petTypeFieldsDto);

    PetTypeDto toPetTypeDto(PetType petType);

    List<PetTypeDto> toPetTypeDtos(List<PetType> petTypes);
}
