package com.pacific.restapi.controller;

import com.pacific.restapi.annotations.ApiController;
import com.pacific.restapi.annotations.DataValidation;
import com.pacific.restapi.dto.GoodsDto;
import com.pacific.restapi.dto.Response;
import com.pacific.restapi.service.GoodsService;
import com.pacific.restapi.util.UrlConstants;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@ApiController
@RequestMapping(UrlConstants.GoodsManagement.ROOT)
public class GoodsController {
    private final GoodsService goodsService;

    public GoodsController(GoodsService goodsService) {
        this.goodsService = goodsService;
    }

    @PostMapping(UrlConstants.GoodsManagement.CREATE)
    @DataValidation
    public Response create(@RequestBody @Valid GoodsDto goodsDto, BindingResult bindingResult, HttpServletResponse httpServletResponse, HttpServletRequest request) {
        return goodsService.create(goodsDto);
    }

    @PutMapping(UrlConstants.GoodsManagement.UPDATE)
    @DataValidation
    public Response update(@PathVariable("id") Long id, @RequestBody @Valid GoodsDto goodsDto, BindingResult bindingResult, HttpServletResponse httpServletResponse, HttpServletRequest request) {
        return goodsService.update(id, goodsDto);
    }

    @DeleteMapping(UrlConstants.GoodsManagement.DELETE)
    public Response delete(@PathVariable("id") Long id, HttpServletResponse httpServletResponse, HttpServletRequest request) {
        return goodsService.delete(id);
    }

    @GetMapping(UrlConstants.GoodsManagement.GET)
    public Response get(@PathVariable("id") Long id, HttpServletResponse httpServletResponse) {
        return goodsService.get(id);
    }

    @GetMapping(UrlConstants.GoodsManagement.GET_BY_NAME)
    public Response getGoodsName(@RequestParam("desc") String description, HttpServletResponse httpServletResponse) {
        return goodsService.getGoodsName(description);
    }

    @GetMapping(UrlConstants.GoodsManagement.GET_ALL)
    public Response getAll(HttpServletResponse httpServletResponse, Pageable pageable,
                           @RequestParam(value = "export", defaultValue = "false") boolean isExport,
                           @RequestParam(value = "search", defaultValue = "") String search,
                           @RequestParam(value = "status", defaultValue = "") String status) {

        return goodsService.getAll(pageable, isExport, search, status);
    }
}
