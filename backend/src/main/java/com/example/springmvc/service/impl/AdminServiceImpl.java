package com.example.springmvc.service.impl;

import com.example.springmvc.dto.DashboardStatsDTO;
import com.example.springmvc.repository.*;
import com.example.springmvc.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@Transactional
public class AdminServiceImpl implements AdminService {

    @Autowired
    private RentTicketRepository rentTicketRepository;

    @Autowired
    private ChemicalRepository chemicalRepository;

    @Autowired
    private AssetRepository assetRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public DashboardStatsDTO getDashboardStats() {
        DashboardStatsDTO stats = new DashboardStatsDTO();
        
        // Đếm phiếu mượn chờ duyệt
        stats.setPendingTicketsCount(
            rentTicketRepository.findPendingTickets().stream().count()
        );
        
        // Đếm hóa chất sắp hết (currentQuantity < 10)
        stats.setLowStockChemicalsCount(
            chemicalRepository.findLowStockChemicals(new BigDecimal("10")).stream().count()
        );
        
        // Đếm tổng thiết bị
        stats.setTotalAssetsCount(assetRepository.count());
        
        // Đếm người dùng đang hoạt động
        stats.setActiveUsersCount(
            userRepository.findAll().stream()
                .filter(user -> user.getIsActive() != null && user.getIsActive())
                .count()
        );
        
        return stats;
    }
}

