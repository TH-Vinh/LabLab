package com.example.springmvc.repository;

import com.example.springmvc.entity.Tool;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ToolRepository extends JpaRepository<Tool, Integer> {
    
    // Tìm tất cả dụng cụ
    @Query("SELECT t FROM Tool t")
    List<Tool> findAllTools();
    
    // Tìm kiếm dụng cụ theo tên
    @Query("SELECT t FROM Tool t WHERE t.name LIKE CONCAT('%', :keyword, '%')")
    List<Tool> searchTools(@Param("keyword") String keyword);
}

