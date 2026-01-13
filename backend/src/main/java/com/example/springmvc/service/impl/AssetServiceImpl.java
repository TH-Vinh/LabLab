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

    @Override
    public AssetResponseDTO importOrUpdateAsset(AssetResponseDTO assetDTO) {
        // Tìm thiết bị theo mã
        java.util.Optional<Asset> existingAssetOpt = assetRepository.findByItemCode(assetDTO.getItemCode());
        
        if (existingAssetOpt.isPresent()) {
            // Nếu đã tồn tại: cập nhật số lượng (cộng thêm)
            Asset existing = existingAssetOpt.get();
            
            // Cập nhật số lượng kế toán và tồn kho (cộng thêm)
            if (assetDTO.getAccountingQuantity() != null) {
                int currentAccounting = existing.getAccountingQuantity() != null ? existing.getAccountingQuantity() : 0;
                existing.setAccountingQuantity(currentAccounting + assetDTO.getAccountingQuantity());
            }
            if (assetDTO.getInventoryQuantity() != null) {
                int currentInventory = existing.getInventoryQuantity() != null ? existing.getInventoryQuantity() : 0;
                existing.setInventoryQuantity(currentInventory + assetDTO.getInventoryQuantity());
            }
            
            // Cập nhật các thông tin khác nếu có
            if (assetDTO.getName() != null && !assetDTO.getName().isEmpty()) {
                existing.setName(assetDTO.getName());
            }
            if (assetDTO.getUnit() != null) {
                existing.setUnit(assetDTO.getUnit());
            }
            if (assetDTO.getYearInUse() != null) {
                existing.setYearInUse(assetDTO.getYearInUse());
            }
            if (assetDTO.getStatusDetail() != null) {
                existing.setStatusDetail(assetDTO.getStatusDetail());
            }
            if (assetDTO.getSupplier() != null) {
                existing.setSupplier(assetDTO.getSupplier());
            }
            if (assetDTO.getStorageLocation() != null) {
                existing.setStorageLocation(assetDTO.getStorageLocation());
            }
            if (assetDTO.getOriginalPrice() != null) {
                existing.setOriginalPrice(assetDTO.getOriginalPrice());
            }
            if (assetDTO.getResidualValue() != null) {
                existing.setResidualValue(assetDTO.getResidualValue());
            }
            
            existing = assetRepository.save(existing);
            return convertToDTO(existing);
        } else {
            // Nếu chưa có: tạo mới
            return createAsset(assetDTO);
        }
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

