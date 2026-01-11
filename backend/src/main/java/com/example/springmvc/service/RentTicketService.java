package com.example.springmvc.service;

import com.example.springmvc.dto.RentTicketResponseDTO;
import com.example.springmvc.dto.UpdateRentTicketStatusDTO;

import java.util.List;

public interface RentTicketService {
    List<RentTicketResponseDTO> getPendingTickets();
    List<RentTicketResponseDTO> getAllTickets(String status);
    RentTicketResponseDTO getTicketById(Integer ticketId);
    void updateTicketStatus(Integer ticketId, UpdateRentTicketStatusDTO updateDTO);
}

