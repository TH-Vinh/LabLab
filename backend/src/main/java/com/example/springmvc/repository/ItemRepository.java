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

    // Tìm kiếm theo tên (và formula nếu là Chemical - xử lý trong service layer)
    @Query("SELECT i FROM Item i WHERE LOWER(i.name) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Item> searchByNameOrFormula(@Param("keyword") String keyword);
}