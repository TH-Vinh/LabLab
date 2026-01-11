package com.example.springmvc.controller;
import com.example.springmvc.dto.ItemResponse;
import com.example.springmvc.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class LabController {

    @Autowired
    private ItemService itemService;

    // --- API 1: LẤY DANH SÁCH VẬT TƯ (TÌM KIẾM & ALL) ---
    // GET /api/items                   -> Lấy tất cả
    // GET /api/items?category=CHEMICAL, DEVICE, TOOL -> Lọc hóa chất
    // GET /api/items?keyword=axit      -> Tìm kiếm
    @GetMapping("/items")
    public List<ItemResponse> getItems(
            @RequestParam(name = "category", required = false, defaultValue = "ALL") String category,
            @RequestParam(name = "keyword", required = false, defaultValue = "") String keyword
    ) {
        return itemService.searchItems(category, keyword);
    }

}