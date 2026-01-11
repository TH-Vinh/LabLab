package com.example.springmvc.controller;

import com.example.springmvc.dto.*;
import com.example.springmvc.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @Autowired
    private RentTicketService rentTicketService;

    @Autowired
    private UserService userService;

    @Autowired
    private ChemicalService chemicalService;

    @Autowired
    private AssetService assetService;

    // ========== DASHBOARD ==========
    @GetMapping("/dashboard/stats")
    public ResponseEntity<DashboardStatsDTO> getDashboardStats() {
        return ResponseEntity.ok(adminService.getDashboardStats());
    }

    // ========== RENT TICKETS ==========
    @GetMapping("/tickets/pending")
    public ResponseEntity<List<RentTicketResponseDTO>> getPendingTickets() {
        return ResponseEntity.ok(rentTicketService.getPendingTickets());
    }

    @GetMapping("/tickets")
    public ResponseEntity<List<RentTicketResponseDTO>> getAllTickets(
            @RequestParam(required = false) String status
    ) {
        return ResponseEntity.ok(rentTicketService.getAllTickets(status));
    }

    @GetMapping("/tickets/{ticketId}")
    public ResponseEntity<RentTicketResponseDTO> getTicketById(@PathVariable Integer ticketId) {
        return ResponseEntity.ok(rentTicketService.getTicketById(ticketId));
    }

    @PutMapping("/tickets/{ticketId}/status")
    public ResponseEntity<?> updateTicketStatus(
            @PathVariable Integer ticketId,
            @RequestBody UpdateRentTicketStatusDTO updateDTO
    ) {
        rentTicketService.updateTicketStatus(ticketId, updateDTO);
        return ResponseEntity.ok().build();
    }

    // ========== USERS ==========
    @GetMapping("/users")
    public ResponseEntity<List<UserResponseDTO>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/users/{userId}")
    public ResponseEntity<UserResponseDTO> getUserById(@PathVariable Integer userId) {
        return ResponseEntity.ok(userService.getUserById(userId));
    }

    @PutMapping("/users/{userId}/status")
    public ResponseEntity<UserResponseDTO> updateUserStatus(
            @PathVariable Integer userId,
            @RequestParam Boolean isActive
    ) {
        return ResponseEntity.ok(userService.updateUserStatus(userId, isActive));
    }

    @DeleteMapping("/users/{userId}")
    public ResponseEntity<?> deleteUser(@PathVariable Integer userId) {
        userService.deleteUser(userId);
        return ResponseEntity.ok().build();
    }

    // ========== CHEMICALS ==========
    @GetMapping("/chemicals")
    public ResponseEntity<List<ChemicalResponseDTO>> getAllChemicals() {
        return ResponseEntity.ok(chemicalService.getAllChemicals());
    }

    @GetMapping("/chemicals/low-stock")
    public ResponseEntity<List<ChemicalResponseDTO>> getLowStockChemicals() {
        return ResponseEntity.ok(chemicalService.getLowStockChemicals());
    }

    @GetMapping("/chemicals/{id}")
    public ResponseEntity<ChemicalResponseDTO> getChemicalById(@PathVariable Integer id) {
        return ResponseEntity.ok(chemicalService.getChemicalById(id));
    }

    @PostMapping("/chemicals")
    public ResponseEntity<ChemicalResponseDTO> createChemical(@RequestBody ChemicalResponseDTO chemicalDTO) {
        return ResponseEntity.ok(chemicalService.createChemical(chemicalDTO));
    }

    @PutMapping("/chemicals/{id}")
    public ResponseEntity<ChemicalResponseDTO> updateChemical(
            @PathVariable Integer id,
            @RequestBody ChemicalResponseDTO chemicalDTO
    ) {
        return ResponseEntity.ok(chemicalService.updateChemical(id, chemicalDTO));
    }

    @DeleteMapping("/chemicals/{id}")
    public ResponseEntity<?> deleteChemical(@PathVariable Integer id) {
        chemicalService.deleteChemical(id);
        return ResponseEntity.ok().build();
    }

    // ========== ASSETS (DEVICES) ==========
    @GetMapping("/assets")
    public ResponseEntity<List<AssetResponseDTO>> getAllAssets() {
        return ResponseEntity.ok(assetService.getAllAssets());
    }

    @GetMapping("/assets/{id}")
    public ResponseEntity<AssetResponseDTO> getAssetById(@PathVariable Integer id) {
        return ResponseEntity.ok(assetService.getAssetById(id));
    }

    @PostMapping("/assets")
    public ResponseEntity<AssetResponseDTO> createAsset(@RequestBody AssetResponseDTO assetDTO) {
        return ResponseEntity.ok(assetService.createAsset(assetDTO));
    }

    @PutMapping("/assets/{id}")
    public ResponseEntity<AssetResponseDTO> updateAsset(
            @PathVariable Integer id,
            @RequestBody AssetResponseDTO assetDTO
    ) {
        return ResponseEntity.ok(assetService.updateAsset(id, assetDTO));
    }

    @DeleteMapping("/assets/{id}")
    public ResponseEntity<?> deleteAsset(@PathVariable Integer id) {
        assetService.deleteAsset(id);
        return ResponseEntity.ok().build();
    }
}

