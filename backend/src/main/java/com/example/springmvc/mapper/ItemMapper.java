package com.example.springmvc.mapper;

import com.example.springmvc.dto.ItemResponse;
import com.example.springmvc.entity.Chemical;
import com.example.springmvc.entity.Item;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ItemMapper {

    ItemResponse toDto(Item item);

    @AfterMapping
    default void mapSpecificFields(@MappingTarget ItemResponse dto, Item item) {

        if (item instanceof Chemical) {
            Chemical c = (Chemical) item;
            dto.setFormula(c.getFormula());
            dto.setPackaging(c.getPackaging());
        }
    }
}