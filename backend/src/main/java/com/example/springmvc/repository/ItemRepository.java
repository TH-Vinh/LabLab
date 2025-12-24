package com.example.springmvc.repository;

import com.example.springmvc.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Integer> {
    List<Item> findByCategoryType(String categoryType);

    List<Item> searchGlobal(@Param("keyword") String keyword);

    List<Item> searchByCategoryAndKeyword(@Param("category") String category, @Param("keyword") String keyword);
}