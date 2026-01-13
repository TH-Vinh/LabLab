package com.example.springmvc.service;

import com.example.springmvc.dto.AssetResponseDTO;

import java.util.List;

public interface AssetService {
    List<AssetResponseDTO> getAllAssets();
    AssetResponseDTO getAssetById(Integer id);
    AssetResponseDTO createAsset(AssetResponseDTO assetDTO);
    AssetResponseDTO updateAsset(Integer id, AssetResponseDTO assetDTO);
    void deleteAsset(Integer id);
    
    /**
     * Import thiết bị: nếu mã đã tồn tại thì cập nhật số lượng (cộng thêm), nếu chưa có thì tạo mới
     */
    AssetResponseDTO importOrUpdateAsset(AssetResponseDTO assetDTO);
}

