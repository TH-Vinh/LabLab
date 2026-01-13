package com.example.springmvc.service.impl;

import com.example.springmvc.dto.ChemicalResponseDTO;
import com.example.springmvc.entity.Chemical;
import com.example.springmvc.entity.Item;
import com.example.springmvc.repository.ChemicalRepository;
import com.example.springmvc.service.ChemicalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ChemicalServiceImpl implements ChemicalService {

    @Autowired
    private ChemicalRepository chemicalRepository;

    @Override
    public List<ChemicalResponseDTO> getAllChemicals() {
        return chemicalRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ChemicalResponseDTO> getLowStockChemicals() {
        return chemicalRepository.findLowStockChemicals(new java.math.BigDecimal("10")).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public ChemicalResponseDTO getChemicalById(Integer id) {
        Chemical chemical = chemicalRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Chemical not found"));
        return convertToDTO(chemical);
    }

    @Override
    public ChemicalResponseDTO createChemical(ChemicalResponseDTO chemicalDTO) {
        try {
            // Kiểm tra mã đã tồn tại chưa
            if (chemicalDTO.getItemCode() != null) {
                java.util.Optional<Chemical> existing = chemicalRepository.findByItemCode(chemicalDTO.getItemCode().trim());
                if (existing.isPresent()) {
                    throw new RuntimeException("Mã hóa chất '" + chemicalDTO.getItemCode() + "' đã tồn tại!");
                }
            }
            
            Chemical chemical = convertToEntity(chemicalDTO);
            chemical = chemicalRepository.save(chemical);
            return convertToDTO(chemical);
        } catch (org.springframework.dao.DataIntegrityViolationException e) {
            throw new RuntimeException("Lỗi khi lưu hóa chất: Mã hóa chất có thể đã tồn tại hoặc dữ liệu không hợp lệ. " + e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi tạo hóa chất: " + e.getMessage(), e);
        }
    }

    @Override
    public ChemicalResponseDTO updateChemical(Integer id, ChemicalResponseDTO chemicalDTO) {
        Chemical chemical = chemicalRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Chemical not found"));
        
        // Update fields
        chemical.setFormula(chemicalDTO.getFormula());
        chemical.setSupplier(chemicalDTO.getSupplier());
        chemical.setPackaging(chemicalDTO.getPackaging());
        chemical.setStorageLocation(chemicalDTO.getStorageLocation());
        chemical.setOriginalPrice(chemicalDTO.getOriginalPrice());
        
        // Update Item fields
        Item item = chemical;
        item.setName(chemicalDTO.getName());
        item.setUnit(chemicalDTO.getUnit());
        item.setCurrentQuantity(chemicalDTO.getCurrentQuantity());
        item.setLockedQuantity(chemicalDTO.getLockedQuantity());
        item.setYearInUse(chemicalDTO.getYearInUse());
        
        chemical = chemicalRepository.save(chemical);
        return convertToDTO(chemical);
    }

    @Override
    public void deleteChemical(Integer id) {
        if (!chemicalRepository.existsById(id)) {
            throw new RuntimeException("Chemical not found");
        }
        chemicalRepository.deleteById(id);
    }

    @Override
    public ChemicalResponseDTO importOrUpdateChemical(ChemicalResponseDTO chemicalDTO) {
        if (chemicalDTO == null) {
            throw new RuntimeException("ChemicalDTO không được null!");
        }
        
        if (chemicalDTO.getItemCode() == null || chemicalDTO.getItemCode().trim().isEmpty()) {
            throw new RuntimeException("Mã hóa chất không được để trống!");
        }
        
        // Tìm hóa chất theo mã
        java.util.Optional<Chemical> existingChemicalOpt = chemicalRepository.findByItemCode(chemicalDTO.getItemCode().trim());
        
        if (existingChemicalOpt.isPresent()) {
            // Nếu đã tồn tại: cập nhật số lượng (cộng thêm)
            Chemical existing = existingChemicalOpt.get();
            java.math.BigDecimal newQuantity = existing.getCurrentQuantity() != null ? existing.getCurrentQuantity() : java.math.BigDecimal.ZERO;
            java.math.BigDecimal importQuantity = chemicalDTO.getCurrentQuantity() != null ? chemicalDTO.getCurrentQuantity() : java.math.BigDecimal.ZERO;
            existing.setCurrentQuantity(newQuantity.add(importQuantity));
            
            // Cập nhật các thông tin khác nếu có
            if (chemicalDTO.getName() != null && !chemicalDTO.getName().isEmpty()) {
                existing.setName(chemicalDTO.getName());
            }
            if (chemicalDTO.getUnit() != null) {
                existing.setUnit(chemicalDTO.getUnit());
            }
            if (chemicalDTO.getFormula() != null) {
                existing.setFormula(chemicalDTO.getFormula());
            }
            if (chemicalDTO.getSupplier() != null) {
                existing.setSupplier(chemicalDTO.getSupplier());
            }
            if (chemicalDTO.getPackaging() != null) {
                existing.setPackaging(chemicalDTO.getPackaging());
            }
            if (chemicalDTO.getStorageLocation() != null) {
                existing.setStorageLocation(chemicalDTO.getStorageLocation());
            }
            if (chemicalDTO.getOriginalPrice() != null) {
                existing.setOriginalPrice(chemicalDTO.getOriginalPrice());
            }
            
            existing = chemicalRepository.save(existing);
            return convertToDTO(existing);
        } else {
            // Nếu chưa có: tạo mới
            return createChemical(chemicalDTO);
        }
    }

    private ChemicalResponseDTO convertToDTO(Chemical chemical) {
        ChemicalResponseDTO dto = new ChemicalResponseDTO();
        dto.setItemId(chemical.getItemId());
        dto.setItemCode(chemical.getItemCode());
        dto.setName(chemical.getName());
        dto.setCategoryType(chemical.getCategoryType());
        dto.setUnit(chemical.getUnit());
        dto.setCurrentQuantity(chemical.getCurrentQuantity());
        dto.setLockedQuantity(chemical.getLockedQuantity());
        dto.setYearInUse(chemical.getYearInUse());
        dto.setCreatedAt(chemical.getCreatedAt());
        
        dto.setFormula(chemical.getFormula());
        dto.setSupplier(chemical.getSupplier());
        dto.setPackaging(chemical.getPackaging());
        dto.setStorageLocation(chemical.getStorageLocation());
        dto.setOriginalPrice(chemical.getOriginalPrice());
        
        return dto;
    }

    private Chemical convertToEntity(ChemicalResponseDTO dto) {
        if (dto == null) {
            throw new RuntimeException("ChemicalDTO không được null!");
        }
        
        if (dto.getItemCode() == null || dto.getItemCode().trim().isEmpty()) {
            throw new RuntimeException("Mã hóa chất không được để trống!");
        }
        
        if (dto.getName() == null || dto.getName().trim().isEmpty()) {
            throw new RuntimeException("Tên hóa chất không được để trống!");
        }
        
        Chemical chemical = new Chemical();
        chemical.setItemCode(dto.getItemCode().trim());
        chemical.setName(dto.getName().trim());
        chemical.setCategoryType("CHEMICAL");
        chemical.setUnit(dto.getUnit() != null ? dto.getUnit().trim() : null);
        chemical.setCurrentQuantity(dto.getCurrentQuantity() != null ? dto.getCurrentQuantity() : java.math.BigDecimal.ZERO);
        chemical.setLockedQuantity(dto.getLockedQuantity() != null ? dto.getLockedQuantity() : java.math.BigDecimal.ZERO);
        chemical.setYearInUse(dto.getYearInUse());
        
        chemical.setFormula(dto.getFormula() != null ? dto.getFormula().trim() : null);
        chemical.setSupplier(dto.getSupplier() != null ? dto.getSupplier().trim() : null);
        chemical.setPackaging(dto.getPackaging() != null ? dto.getPackaging().trim() : null);
        chemical.setStorageLocation(dto.getStorageLocation() != null ? dto.getStorageLocation().trim() : null);
        chemical.setOriginalPrice(dto.getOriginalPrice());
        
        return chemical;
    }
}

