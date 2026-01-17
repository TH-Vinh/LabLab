package com.example.springmvc.mapper;

import com.example.springmvc.dto.rent.RentListResponse;
import com.example.springmvc.entity.RentTicket;
import com.example.springmvc.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface RentMapper {

    @Mapping(source = "room.roomName", target = "roomName")
    @Mapping(source = "user", target = "borrowerName", qualifiedByName = "getBorrowerName")
    @Mapping(source = "createdDate", target = "createdDate")
    RentListResponse toRentListResponse(RentTicket entity);

    @Named("getBorrowerName")
    default String getBorrowerName(User user) {
        if (user == null) return "N/A";
        if (user.getProfile() != null && user.getProfile().getFullName() != null) {
            return user.getProfile().getFullName();
        }
        return user.getUsername();
    }
}