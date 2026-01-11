package com.example.springmvc.service.impl;

import com.example.springmvc.dto.AssetResponseDTO;
import com.example.springmvc.entity.Asset;
import com.example.springmvc.entity.Item;
import com.example.springmvc.repository.AssetRepository;
import com.example.springmvc.service.AssetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class AssetServiceImpl implements AssetService {

    @Autowired
    private AssetRepository assetRepository;

    @Override
    public List<AssetResponseDTO> getAllAssets() {
        return assetRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public AssetResponseDTO getAssetById(Integer id) {
        Asset asset = assetRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Asset not found"));
        return convertToDTO(asset);
    }

    @Override
    public AssetResponseDTO createAsset(AssetResponseDTO assetDTO) {
        Asset asset = convertToEntity(assetDTO);
        asset = assetRepository.save(asset);
        return convertToDTO(asset);
    }

    @Override
    public AssetResponseDTO updateAsset(Integer id, AssetResponseDTO assetDTO) {
        Asset asset = assetRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Asset not found"));
        
        // Update Asset specific fields
        asset.setResidualValue(assetDTO.getResidualValue());
        asset.setAccountingQuantity(assetDTO.getAccountingQuantity());
        asset.setInventoryQuantity(assetDTO.getInventoryQuantity());
        asset.setStatusDetail(assetDTO.getStatusDetail());
        asset.setSupplier(assetDTO.getSupplier());
        asset.setStorageLocation(assetDTO.getStorageLocation());
        asset.setOriginalPrice(assetDTO.getOriginalPrice());
        
        // Update Item fields
        Item item = asset;
        item.setName(assetDTO.getName());
        item.setUnit(assetDTO.getUnit());
        item.setYearInUse(assetDTO.getYearInUse());
        
        asset = assetRepository.save(asset);
        return convertToDTO(asset);
    }

    @Override
    public void deleteAsset(Integer id) {
        if (!assetRepository.existsById(id)) {
            throw new RuntimeException("Asset not found");
        }
        assetRepository.deleteById(id);
    }

    private AssetResponseDTO convertToDTO(Asset asset) {
        AssetResponseDTO dto = new AssetResponseDTO();
        dto.setItemId(asset.getItemId());
        dto.setItemCode(asset.getItemCode());
        dto.setName(asset.getName());
        dto.setCategoryType(asset.getCategoryType());
        dto.setUnit(asset.getUnit());
        dto.setYearInUse(asset.getYearInUse());
        dto.setCreatedAt(asset.getCreatedAt());
        
        dto.setResidualValue(asset.getResidualValue());
        dto.setAccountingQuantity(asset.getAccountingQuantity());
        dto.setInventoryQuantity(asset.getInventoryQuantity());
        dto.setStatusDetail(asset.getStatusDetail());
        dto.setSupplier(asset.getSupplier());
        dto.setStorageLocation(asset.getStorageLocation());
        dto.setOriginalPrice(asset.getOriginalPrice());
        
        return dto;
    }

    private Asset convertToEntity(AssetResponseDTO dto) {
        Asset asset = new Asset();
        asset.setItemCode(dto.getItemCode());
        asset.setName(dto.getName());
        asset.setCategoryType("DEVICE");
        asset.setUnit(dto.getUnit());
        asset.setYearInUse(dto.getYearInUse());
        
        asset.setResidualValue(dto.getResidualValue());
        asset.setAccountingQuantity(dto.getAccountingQuantity());
        asset.setInventoryQuantity(dto.getInventoryQuantity());
        asset.setStatusDetail(dto.getStatusDetail());
        asset.setSupplier(dto.getSupplier());
        asset.setStorageLocation(dto.getStorageLocation());
        asset.setOriginalPrice(dto.getOriginalPrice());
        
        return asset;
    }
}

