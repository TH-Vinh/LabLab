package com.example.springmvc.controller;

import com.example.springmvc.dto.ItemResponse;
import com.example.springmvc.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class LabController {

    @Autowired
    private ItemService itemService;

    @GetMapping("/items")
    public ResponseEntity<List<ItemResponse>> getItems(
            @RequestParam(name = "category", required = false, defaultValue = "ALL") String category,
            @RequestParam(name = "keyword", required = false, defaultValue = "") String keyword
    ) {
        List<ItemResponse> result = itemService.searchItems(category, keyword);
        return ResponseEntity.ok(result);
    }
}