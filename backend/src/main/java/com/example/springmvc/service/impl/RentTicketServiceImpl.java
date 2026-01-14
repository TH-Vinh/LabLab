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
        return rentTicketRepository.findByStatusOrderByCreatedDateDesc("PENDING").stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<RentTicketResponseDTO> getAllTickets(String status) {
        List<RentTicket> tickets;
        if (status != null && !status.isEmpty()) {
            tickets = rentTicketRepository.findByStatusOrderByCreatedDateDesc(status);
        } else {
            tickets = rentTicketRepository.findAllByOrderByCreatedDateDesc();
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
        if (ticket == null) {
            throw new RuntimeException("Ticket is null");
        }
        
        RentTicketResponseDTO dto = new RentTicketResponseDTO();
        dto.setTicketId(ticket.getTicketId());
        
        // Handle user safely
        if (ticket.getUser() != null) {
            dto.setUserId(ticket.getUser().getUserId());
            dto.setUsername(ticket.getUser().getUsername());
            
            if (ticket.getUser().getProfile() != null) {
                dto.setFullName(ticket.getUser().getProfile().getFullName());
            }
        }
        
        // Handle room safely
        if (ticket.getRoom() != null) {
            dto.setRoomId(ticket.getRoom().getRoomId());
            dto.setRoomName(ticket.getRoom().getRoomName());
        }
        
        dto.setCreatedDate(ticket.getCreatedDate());
        dto.setBorrowDate(ticket.getBorrowDate());
        dto.setExpectedReturnDate(ticket.getExpectedReturnDate());
        dto.setStatus(ticket.getStatus());
        
        // Convert details safely
        if (ticket.getDetails() != null && !ticket.getDetails().isEmpty()) {
            dto.setDetails(ticket.getDetails().stream()
                    .map(this::convertDetailToDTO)
                    .filter(detail -> detail != null)
                    .collect(Collectors.toList()));
        }
        
        return dto;
    }

    private RentTicketResponseDTO.RentTicketDetailDTO convertDetailToDTO(RentTicketDetail detail) {
        if (detail == null) {
            return null;
        }
        
        RentTicketResponseDTO.RentTicketDetailDTO dto = new RentTicketResponseDTO.RentTicketDetailDTO();
        dto.setDetailId(detail.getDetailId());
        
        // Handle item safely
        if (detail.getItem() != null) {
            dto.setItemId(detail.getItem().getItemId());
            dto.setItemName(detail.getItem().getName());
            dto.setItemCode(detail.getItem().getItemCode());
            dto.setCategoryType(detail.getItem().getCategoryType());
            dto.setUnit(detail.getItem().getUnit());
        }
        
        dto.setQuantity(detail.getQuantityBorrowed());
        dto.setReturnStatus(detail.getReturnStatus());
        return dto;
    }
}

