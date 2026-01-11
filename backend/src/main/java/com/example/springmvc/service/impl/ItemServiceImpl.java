package com.example.springmvc.service.impl;

import com.example.springmvc.dto.ItemResponse;
import com.example.springmvc.entity.*;
import com.example.springmvc.repository.ItemRepository;
import com.example.springmvc.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ItemServiceImpl implements ItemService {

    @Autowired
    private ItemRepository itemRepository;

    @Override
    public List<ItemResponse> searchItems(String category, String keyword) {
        List<Item> items;
        if (keyword != null && !keyword.trim().isEmpty()) {
            items = itemRepository.searchByNameOrFormula(keyword.trim());
        } else if (category != null && !category.equalsIgnoreCase("ALL")) {
            items = itemRepository.findByCategoryType(category);
        } else {
            items = itemRepository.findAll();
        }
        return items.stream().map(this::convertToDto).collect(Collectors.toList());
    }

    private ItemResponse convertToDto(Item item) {
        ItemResponse dto = new ItemResponse();

        dto.setItemId(item.getItemId());
        dto.setName(item.getName());
        dto.setCategoryType(item.getCategoryType());
        dto.setUnit(item.getUnit());
        dto.setYearInUse(item.getYearInUse());

        if (item instanceof Chemical) {
            Chemical chem = (Chemical) item;
            dto.setFormula(chem.getFormula());
            dto.setPackaging(chem.getPackaging());
            dto.setSupplier(chem.getSupplier());
        } else if (item instanceof Asset) {
            Asset asset = (Asset) item;
            dto.setSupplier(asset.getSupplier());
        } else if (item instanceof Tool) {
            Tool tool = (Tool) item;
            dto.setSupplier(tool.getSupplier());
        }

        return dto;
    }
}