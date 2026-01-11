package com.example.springmvc.service.impl;

import com.example.springmvc.dto.RentTicketResponseDTO;
import com.example.springmvc.dto.UpdateRentTicketStatusDTO;
import com.example.springmvc.entity.RentTicket;
import com.example.springmvc.entity.RentTicketDetail;
import com.example.springmvc.repository.RentTicketRepository;
import com.example.springmvc.service.RentTicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class RentTicketServiceImpl implements RentTicketService {

    @Autowired
    private RentTicketRepository rentTicketRepository;

    @Override
    public List<RentTicketResponseDTO> getPendingTickets() {
        return rentTicketRepository.findPendingTickets().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<RentTicketResponseDTO> getAllTickets(String status) {
        List<RentTicket> tickets;
        if (status != null && !status.isEmpty()) {
            tickets = rentTicketRepository.findByStatus(status);
        } else {
            tickets = rentTicketRepository.findAll();
        }
        
        return tickets.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public RentTicketResponseDTO getTicketById(Integer ticketId) {
        RentTicket ticket = rentTicketRepository.findById(ticketId)
                .orElseThrow(() -> new RuntimeException("Ticket not found"));
        return convertToDTO(ticket);
    }

    @Override
    public void updateTicketStatus(Integer ticketId, UpdateRentTicketStatusDTO updateDTO) {
        RentTicket ticket = rentTicketRepository.findById(ticketId)
                .orElseThrow(() -> new RuntimeException("Ticket not found"));
        
        ticket.setStatus(updateDTO.getStatus());
        rentTicketRepository.save(ticket);
    }

    private RentTicketResponseDTO convertToDTO(RentTicket ticket) {
        RentTicketResponseDTO dto = new RentTicketResponseDTO();
        dto.setTicketId(ticket.getTicketId());
        dto.setUserId(ticket.getUser().getUserId());
        dto.setUsername(ticket.getUser().getUsername());
        
        if (ticket.getUser().getProfile() != null) {
            dto.setFullName(ticket.getUser().getProfile().getFullName());
        }
        
        if (ticket.getRoom() != null) {
            dto.setRoomId(ticket.getRoom().getRoomId());
            dto.setRoomName(ticket.getRoom().getRoomName());
        }
        
        dto.setCreatedDate(ticket.getCreatedDate());
        dto.setExpectedReturnDate(ticket.getExpectedReturnDate());
        dto.setStatus(ticket.getStatus());
        
        // Convert details
        if (ticket.getDetails() != null) {
            dto.setDetails(ticket.getDetails().stream()
                    .map(this::convertDetailToDTO)
                    .collect(Collectors.toList()));
        }
        
        return dto;
    }

    private RentTicketResponseDTO.RentTicketDetailDTO convertDetailToDTO(RentTicketDetail detail) {
        RentTicketResponseDTO.RentTicketDetailDTO dto = new RentTicketResponseDTO.RentTicketDetailDTO();
        dto.setDetailId(detail.getDetailId());
        dto.setItemId(detail.getItem().getItemId());
        dto.setItemName(detail.getItem().getName());
        dto.setItemCode(detail.getItem().getItemCode());
        dto.setCategoryType(detail.getItem().getCategoryType());
        dto.setQuantity(detail.getQuantity());
        dto.setUnit(detail.getItem().getUnit());
        dto.setReturnStatus(detail.getReturnStatus());
        return dto;
    }
}

