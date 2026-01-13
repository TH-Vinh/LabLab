package com.example.springmvc.service;

import com.example.springmvc.dto.AssetResponseDTO;
import com.example.springmvc.dto.ChemicalResponseDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ExcelService {
    /**
     * Export danh sách hóa chất ra file Excel
     */
    byte[] exportChemicalsToExcel(List<ChemicalResponseDTO> chemicals);

    /**
     * Export danh sách thiết bị ra file Excel
     */
    byte[] exportAssetsToExcel(List<AssetResponseDTO> assets);

    /**
     * Import hóa chất từ file Excel
     */
    List<ChemicalResponseDTO> importChemicalsFromExcel(MultipartFile file);

    /**
     * Import thiết bị từ file Excel
     */
    List<AssetResponseDTO> importAssetsFromExcel(MultipartFile file);
}
