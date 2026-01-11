package com.example.springmvc.repository;

import com.example.springmvc.entity.Chemical;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChemicalRepository extends JpaRepository<Chemical, Integer> {
    
    // Tìm tất cả hóa chất
    @Query("SELECT c FROM Chemical c")
    List<Chemical> findAllChemicals();
    
    // Tìm hóa chất sắp hết (currentQuantity < threshold)
    @Query("SELECT c FROM Chemical c WHERE c.currentQuantity < :threshold")
    List<Chemical> findLowStockChemicals(@Param("threshold") java.math.BigDecimal threshold);
    
    // Tìm kiếm hóa chất theo tên hoặc công thức
    @Query("SELECT c FROM Chemical c WHERE c.name LIKE CONCAT('%', :keyword, '%') OR c.formula LIKE CONCAT('%', :keyword, '%')")
    List<Chemical> searchChemicals(@Param("keyword") String keyword);
}

