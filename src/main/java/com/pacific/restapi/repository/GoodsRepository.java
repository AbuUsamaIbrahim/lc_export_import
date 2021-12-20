package com.pacific.restapi.repository;

import com.pacific.restapi.model.Goods;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GoodsRepository extends JpaRepository<Goods, Long> {
    Goods findByIdAndIsActiveTrue(Long id);

    Goods findByDescriptionAndIsActiveTrue(String desc);

    List<Goods> findAllByIsActiveTrue();

    int countByDescriptionAndIsActiveTrue(String desc);
}
