package com.expiryeye.repository;

import com.expiryeye.model.ExpiryItem;
import com.expiryeye.model.ItemCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<ExpiryItem, Long> {
    List<ExpiryItem> findByCategory(ItemCategory category);
    List<ExpiryItem> findByExpiryDateLessThanEqual(LocalDate date);
}
