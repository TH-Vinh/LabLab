package com.example.springmvc.service;

import com.example.springmvc.dto.AssetResponseDTO;

import java.util.List;

public interface AssetService {
    List<AssetResponseDTO> getAllAssets();
    AssetResponseDTO getAssetById(Integer id);
    AssetResponseDTO createAsset(AssetResponseDTO assetDTO);
    AssetResponseDTO updateAsset(Integer id, AssetResponseDTO assetDTO);
    void deleteAsset(Integer id);
}

