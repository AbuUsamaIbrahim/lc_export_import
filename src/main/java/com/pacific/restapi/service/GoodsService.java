package com.pacific.restapi.service;

import com.pacific.restapi.dto.GoodsDto;
import com.pacific.restapi.dto.Response;
import org.springframework.data.domain.Pageable;

public interface GoodsService {
    Response create(GoodsDto goodsDto);

    Response update(Long id, GoodsDto goodsDto);

    Response delete(Long id);

    Response get(Long id);

    Response getAll(Pageable pageable, boolean isExport, String search, String status);

    Response getGoodsName(String description);
}
