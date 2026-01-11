package com.example.springmvc.service;

import com.example.springmvc.dto.ItemResponse;
import java.util.List;

public interface ItemService {
    List<ItemResponse> searchItems(String category, String keyword);
}