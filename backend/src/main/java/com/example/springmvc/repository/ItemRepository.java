package com.example.springmvc.repository;

import com.example.springmvc.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Integer> {
    // Tìm kiếm theo loại (CHEMICAL, DEVICE, TOOL)
    List<Item> findByCategoryType(String categoryType);

    List<Item> searchByNameOrFormula(@Param("keyword") String keyword);
}