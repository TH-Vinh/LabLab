package com.example.springmvc.mapper;

import com.example.springmvc.dto.ItemResponse;
import com.example.springmvc.entity.Asset;
import com.example.springmvc.entity.Chemical;
import com.example.springmvc.entity.Item;
import com.example.springmvc.entity.Tool;
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
            dto.setSupplier(c.getSupplier());
        }
        else if (item instanceof Tool) {
            Tool t = (Tool) item;
            dto.setSupplier(t.getSupplier());
        }
        else if (item instanceof Asset) {
            Asset a = (Asset) item;
            dto.setSupplier(a.getSupplier());
        }
    }
}