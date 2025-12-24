package com.example.springmvc.service;

import com.example.springmvc.dto.ItemResponse;
import com.example.springmvc.entity.Item;
import java.util.List;

public interface ItemService {
    /**
     * Tìm kiếm vật tư (Hóa chất, Thiết bị, Dụng cụ)
     * @param category Loại (CHEMICAL, DEVICE, TOOL)
     * @param keyword Từ khóa tìm kiếm theo tên
     * @return Danh sách Item
     */
    List<ItemResponse> searchItems(String category, String keyword);
}