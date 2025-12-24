package com.example.springmvc.service.impl;

import com.example.springmvc.dto.ItemResponse;
import com.example.springmvc.entity.Item;
import com.example.springmvc.mapper.ItemMapper; // Import Mapper mới
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

    @Autowired
    private ItemMapper itemMapper;

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
        return items.stream()
                .map(itemMapper::toDto)
                .collect(Collectors.toList());
    }

}