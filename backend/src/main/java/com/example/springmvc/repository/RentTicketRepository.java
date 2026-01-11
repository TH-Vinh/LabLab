package com.example.springmvc.repository;

import com.example.springmvc.entity.RentTicket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RentTicketRepository extends JpaRepository<RentTicket, Integer> {
    
    // Tìm các phiếu mượn theo status
    List<RentTicket> findByStatus(String status);
    
    // Tìm các phiếu mượn chờ duyệt
    @Query("SELECT rt FROM RentTicket rt WHERE rt.status = 'PENDING' ORDER BY rt.createdDate DESC")
    List<RentTicket> findPendingTickets();
    
    // Tìm các phiếu mượn của một user
    List<RentTicket> findByUser_UserId(Integer userId);
}

