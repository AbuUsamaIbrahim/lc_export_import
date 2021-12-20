package com.pacific.restapi.repository;

import com.pacific.restapi.model.SalesPriceHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SalesPriceHistoryRepository extends JpaRepository<SalesPriceHistory, Long> {
    List<SalesPriceHistory> findAllBySalesIdAndIsActiveTrue(Long salesId);
}
