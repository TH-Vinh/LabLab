package com.example.springmvc.mapper;

import com.example.springmvc.dto.rent.RentListResponse;
import com.example.springmvc.entity.RentTicket;
import com.example.springmvc.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface RentMapper {

    // Map tự động các trường trùng tên
    // Map trường khác tên: room.roomName -> roomName
    @Mapping(source = "room.roomName", target = "roomName")
    // Map trường xử lý logic phức tạp: Lấy tên người mượn
    @Mapping(source = "user", target = "borrowerName", qualifiedByName = "getBorrowerName")
    RentListResponse toRentListResponse(RentTicket entity);

    // Logic lấy tên người mượn (Ưu tiên Profile, không có thì lấy Username)
    @Named("getBorrowerName")
    default String getBorrowerName(User user) {
        if (user == null) return "Unknown";
        if (user.getProfile() != null && user.getProfile().getFullName() != null) {
            return user.getProfile().getFullName();
        }
        return user.getUsername();
    }
}