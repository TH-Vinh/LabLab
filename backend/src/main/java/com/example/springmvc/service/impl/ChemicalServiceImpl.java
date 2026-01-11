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
        Chemical chemical = convertToEntity(chemicalDTO);
        chemical = chemicalRepository.save(chemical);
        return convertToDTO(chemical);
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
        Chemical chemical = new Chemical();
        chemical.setItemCode(dto.getItemCode());
        chemical.setName(dto.getName());
        chemical.setCategoryType("CHEMICAL");
        chemical.setUnit(dto.getUnit());
        chemical.setCurrentQuantity(dto.getCurrentQuantity());
        chemical.setLockedQuantity(dto.getLockedQuantity() != null ? dto.getLockedQuantity() : java.math.BigDecimal.ZERO);
        chemical.setYearInUse(dto.getYearInUse());
        
        chemical.setFormula(dto.getFormula());
        chemical.setSupplier(dto.getSupplier());
        chemical.setPackaging(dto.getPackaging());
        chemical.setStorageLocation(dto.getStorageLocation());
        chemical.setOriginalPrice(dto.getOriginalPrice());
        
        return chemical;
    }
}

