package com.example.springmvc.controller;

import com.example.springmvc.dto.*;
import com.example.springmvc.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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

    @Autowired
    private ExcelService excelService;

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
            @PathVariable("ticketId") Integer ticketId,
            @RequestBody UpdateRentTicketStatusDTO updateDTO
    ) {
        try {
            rentTicketService.updateTicketStatus(ticketId, updateDTO);
            return ResponseEntity.ok(ApiResponse.success("Cập nhật trạng thái phiếu mượn thành công!"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(ApiResponse.error("Lỗi server: " + e.getMessage()));
        }
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
    public ResponseEntity<?> updateUserStatus(
            @PathVariable("userId") Integer userId,
            @RequestParam("isActive") Boolean isActive
    ) {
        try {
            UserResponseDTO updatedUser = userService.updateUserStatus(userId, isActive);
            return ResponseEntity.ok(updatedUser);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(ApiResponse.error("Lỗi server: " + e.getMessage()));
        }
    }

    @DeleteMapping("/users/{userId}")
    public ResponseEntity<?> deleteUser(@PathVariable Integer userId) {
        try {
            // Lấy username hiện tại từ SecurityContext
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String currentUsername = null;
            if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
                currentUsername = ((UserDetails) authentication.getPrincipal()).getUsername();
            }
            
            userService.deleteUser(userId, currentUsername);
            return ResponseEntity.ok(ApiResponse.success("Xóa người dùng thành công!"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(ApiResponse.error("Lỗi server: " + e.getMessage()));
        }
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
    public ResponseEntity<?> updateChemical(
            @PathVariable("id") Integer id,
            @RequestBody ChemicalResponseDTO chemicalDTO
    ) {
        try {
            ChemicalResponseDTO updated = chemicalService.updateChemical(id, chemicalDTO);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(ApiResponse.error("Lỗi server: " + e.getMessage()));
        }
    }

    @DeleteMapping("/chemicals/{id}")
    public ResponseEntity<?> deleteChemical(@PathVariable("id") Integer id) {
        try {
            chemicalService.deleteChemical(id);
            return ResponseEntity.ok(ApiResponse.success("Xóa hóa chất thành công!"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(ApiResponse.error("Lỗi server: " + e.getMessage()));
        }
    }

    @GetMapping("/chemicals/export")
    public ResponseEntity<byte[]> exportChemicals() {
        try {
            List<ChemicalResponseDTO> chemicals = chemicalService.getAllChemicals();
            byte[] excelData = excelService.exportChemicalsToExcel(chemicals);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", "hoa-chat.xlsx");
            
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(excelData);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).build();
        }
    }

    @PostMapping("/chemicals/import")
    public ResponseEntity<?> importChemicals(@RequestParam("file") MultipartFile file) {
        try {
            if (file == null || file.isEmpty()) {
                return ResponseEntity.badRequest().body(ApiResponse.error("File không được để trống!"));
            }
            
            String fileName = file.getOriginalFilename();
            if (fileName == null || (!fileName.endsWith(".xlsx") && !fileName.endsWith(".xls"))) {
                return ResponseEntity.badRequest().body(ApiResponse.error("File phải là định dạng Excel (.xlsx hoặc .xls)!"));
            }
            
            System.out.println("Bắt đầu import file Excel: " + fileName);
            List<ChemicalResponseDTO> chemicals = excelService.importChemicalsFromExcel(file);
            System.out.println("Đã đọc được " + (chemicals != null ? chemicals.size() : 0) + " hóa chất từ file Excel");
            
            if (chemicals == null || chemicals.isEmpty()) {
                return ResponseEntity.badRequest().body(ApiResponse.error("Không tìm thấy dữ liệu hợp lệ trong file Excel!"));
            }
            
            if (chemicals == null || chemicals.isEmpty()) {
                return ResponseEntity.badRequest().body(ApiResponse.error("Không tìm thấy dữ liệu hợp lệ trong file Excel!"));
            }
            
            // Lưu từng hóa chất vào database
            int successCount = 0;
            int updateCount = 0;
            int createCount = 0;
            int errorCount = 0;
            StringBuilder errors = new StringBuilder();
            
            // Lấy danh sách mã hóa chất hiện có để kiểm tra nhanh
            java.util.Set<String> existingCodes = chemicalService.getAllChemicals().stream()
                    .map(ChemicalResponseDTO::getItemCode)
                    .filter(code -> code != null && !code.isEmpty())
                    .collect(java.util.stream.Collectors.toSet());
            
            for (ChemicalResponseDTO chemical : chemicals) {
                try {
                    if (chemical.getItemCode() == null || chemical.getItemCode().isEmpty()) {
                        errorCount++;
                        errors.append("Lỗi: Mã hóa chất không được để trống\n");
                        continue;
                    }
                    
                    // Kiểm tra xem đã tồn tại chưa
                    boolean existed = existingCodes.contains(chemical.getItemCode());
                    
                    chemicalService.importOrUpdateChemical(chemical);
                    successCount++;
                    if (existed) {
                        updateCount++;
                    } else {
                        createCount++;
                        existingCodes.add(chemical.getItemCode()); // Thêm vào set để tránh duplicate trong cùng lần import
                    }
                } catch (Exception e) {
                    errorCount++;
                    String errorMsg = e.getMessage();
                    if (e.getCause() != null) {
                        errorMsg += " - " + e.getCause().getMessage();
                    }
                    errors.append("Lỗi khi import ").append(chemical.getItemCode() != null ? chemical.getItemCode() : "N/A")
                          .append(": ").append(errorMsg).append("\n");
                    System.err.println("Lỗi khi import hóa chất: " + errorMsg);
                    e.printStackTrace();
                }
            }
            
            String message = String.format("Import thành công %d hóa chất (Tạo mới: %d, Cập nhật: %d)", 
                    successCount, createCount, updateCount);
            if (errorCount > 0) {
                message += String.format(". %d lỗi. Chi tiết: %s", errorCount, errors.toString());
            }
            
            return ResponseEntity.ok(ApiResponse.success(message));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(ApiResponse.error("Lỗi khi import file: " + e.getMessage()));
        }
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
    public ResponseEntity<?> updateAsset(
            @PathVariable("id") Integer id,
            @RequestBody AssetResponseDTO assetDTO
    ) {
        try {
            AssetResponseDTO updated = assetService.updateAsset(id, assetDTO);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(ApiResponse.error("Lỗi server: " + e.getMessage()));
        }
    }

    @DeleteMapping("/assets/{id}")
    public ResponseEntity<?> deleteAsset(@PathVariable("id") Integer id) {
        try {
            assetService.deleteAsset(id);
            return ResponseEntity.ok(ApiResponse.success("Xóa thiết bị thành công!"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(ApiResponse.error("Lỗi server: " + e.getMessage()));
        }
    }

    @GetMapping("/assets/export")
    public ResponseEntity<byte[]> exportAssets() {
        try {
            List<AssetResponseDTO> assets = assetService.getAllAssets();
            byte[] excelData = excelService.exportAssetsToExcel(assets);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", "thiet-bi.xlsx");
            
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(excelData);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).build();
        }
    }

    @PostMapping("/assets/import")
    public ResponseEntity<?> importAssets(@RequestParam("file") MultipartFile file) {
        try {
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body(ApiResponse.error("File không được để trống!"));
            }
            
            List<AssetResponseDTO> assets = excelService.importAssetsFromExcel(file);
            
            // Lưu từng thiết bị vào database
            int successCount = 0;
            int updateCount = 0;
            int createCount = 0;
            int errorCount = 0;
            StringBuilder errors = new StringBuilder();
            
            // Lấy danh sách mã thiết bị hiện có để kiểm tra nhanh
            java.util.Set<String> existingCodes = assetService.getAllAssets().stream()
                    .map(AssetResponseDTO::getItemCode)
                    .filter(code -> code != null && !code.isEmpty())
                    .collect(java.util.stream.Collectors.toSet());
            
            for (AssetResponseDTO asset : assets) {
                try {
                    if (asset.getItemCode() == null || asset.getItemCode().isEmpty()) {
                        errorCount++;
                        errors.append("Lỗi: Mã thiết bị không được để trống\n");
                        continue;
                    }
                    
                    // Kiểm tra xem đã tồn tại chưa
                    boolean existed = existingCodes.contains(asset.getItemCode());
                    
                    assetService.importOrUpdateAsset(asset);
                    successCount++;
                    if (existed) {
                        updateCount++;
                    } else {
                        createCount++;
                        existingCodes.add(asset.getItemCode()); // Thêm vào set để tránh duplicate trong cùng lần import
                    }
                } catch (Exception e) {
                    errorCount++;
                    errors.append("Lỗi khi import ").append(asset.getItemCode() != null ? asset.getItemCode() : "N/A")
                          .append(": ").append(e.getMessage()).append("\n");
                }
            }
            
            String message = String.format("Import thành công %d thiết bị (Tạo mới: %d, Cập nhật: %d)", 
                    successCount, createCount, updateCount);
            if (errorCount > 0) {
                message += String.format(". %d lỗi. Chi tiết: %s", errorCount, errors.toString());
            }
            
            return ResponseEntity.ok(ApiResponse.success(message));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(ApiResponse.error("Lỗi khi import file: " + e.getMessage()));
        }
    }
}

