package com.example.springmvc.service;

import com.example.springmvc.dto.ChemicalResponseDTO;

import java.util.List;

public interface ChemicalService {
    List<ChemicalResponseDTO> getAllChemicals();
    List<ChemicalResponseDTO> getLowStockChemicals();
    ChemicalResponseDTO getChemicalById(Integer id);
    ChemicalResponseDTO createChemical(ChemicalResponseDTO chemicalDTO);
    ChemicalResponseDTO updateChemical(Integer id, ChemicalResponseDTO chemicalDTO);
    void deleteChemical(Integer id);
}

