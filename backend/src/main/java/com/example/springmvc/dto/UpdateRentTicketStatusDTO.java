package com.example.springmvc.dto;

import lombok.Data;

@Data
public class UpdateRentTicketStatusDTO {
    private String status; // APPROVED, REJECTED, RETURNED
    private String reason; // Optional reason for rejection
}

