package com.pacific.restapi.service.impl;

import com.pacific.restapi.dto.GoodsDto;
import com.pacific.restapi.dto.Response;
import com.pacific.restapi.model.Goods;
import com.pacific.restapi.repository.GoodsRepository;
import com.pacific.restapi.service.GoodsService;
import com.pacific.restapi.service.UtilityService;
import com.pacific.restapi.util.ResponseBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.modelmapper.Conditions;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service("goodsService")
public class GoodsServiceImpl implements GoodsService {
    private static final Logger logger = LogManager.getLogger(GoodsServiceImpl.class.getName());
    private final GoodsRepository goodsRepository;
    private final String root = "Lookup Data";
    private final ModelMapper modelMapper;
    private final UtilityService utilityService;

    @PersistenceContext
    private EntityManager entityManager;

    public GoodsServiceImpl(GoodsRepository goodsRepository, ModelMapper modelMapper, UtilityService utilityService) {
        this.goodsRepository = goodsRepository;
        this.modelMapper = modelMapper;
        this.utilityService = utilityService;
    }

    @Override
    @Transactional
    public Response create(GoodsDto goodsDto) {
        goodsDto.setDescription(goodsDto.getDescription().trim());
        if (goodsRepository.countByDescriptionAndIsActiveTrue(goodsDto.getDescription()) > 0) {
            return ResponseBuilder.getFailResponse(HttpStatus.BAD_REQUEST, "Goods Already Exist");
        }
        Goods goods = modelMapper.map(goodsDto, Goods.class);
        return utilityService.getCreateResponse(goods, goodsRepository);
    }

    @Override
    @Transactional
    public Response update(Long id, GoodsDto goodsDto) {
        Response notFoundFailureResponse = utilityService.getNullResponse(goodsRepository, id);
        if (notFoundFailureResponse != null) {
            return notFoundFailureResponse;
        }
        try {
            goodsDto.setDescription(goodsDto.getDescription().trim());
            Goods existingGoods = goodsRepository.findByDescriptionAndIsActiveTrue(goodsDto.getDescription());
            if (existingGoods != null && !existingGoods.getId().equals(id)) {
                return ResponseBuilder.getFailResponse(HttpStatus.BAD_REQUEST, "Already exist with this description");
            }
            Goods goods = utilityService.getById(goodsRepository, id);
            return utilityService.getUpdateResponse(goods, goodsDto, goodsRepository);
        } catch (NullPointerException e) {
            logger.error(e.getMessage());
            return ResponseBuilder.getFailResponse(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            logger.error(e.getMessage());
            return ResponseBuilder.getFailResponse(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @Override
    @Transactional
    public Response delete(Long id) {
        Response notFoundFailureResponse = utilityService.getNullResponse(goodsRepository, id);
        if (notFoundFailureResponse != null) {
            return notFoundFailureResponse;
        }
        try {
            Goods goods = utilityService.getById(goodsRepository, id);
            return utilityService.deleteEntityResponse(goods, goodsRepository);
        } catch (NullPointerException e) {
            logger.error(e.getMessage());
            return ResponseBuilder.getFailResponse(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            logger.error(e.getMessage());
            return ResponseBuilder.getFailResponse(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @Override
    @Transactional
    public Response get(Long id) {
        Response notFoundFailureResponse = utilityService.getNullResponse(goodsRepository, id);
        if (notFoundFailureResponse != null) {
            return notFoundFailureResponse;
        }
        try {
            Goods goods = utilityService.getById(goodsRepository, id);
            GoodsDto goodsDto = modelMapper.map(goods, GoodsDto.class);
            return utilityService.getGetResponse(goodsDto, root);
        } catch (NullPointerException e) {
            logger.error(e.getMessage());
            return ResponseBuilder.getFailResponse(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            logger.error(e.getMessage());
            return ResponseBuilder.getFailResponse(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @Override
    @Transactional
    public Response getAll(Pageable pageable, boolean isExport, String search, String status) {
        List<Goods> goodsList = goodsRepository.findAllByIsActiveTrue();
        List<GoodsDto> goodsDtos = new ArrayList<>();
        goodsList.forEach(goods -> {
            GoodsDto goodsDto = modelMapper.map(goods, GoodsDto.class);
            goodsDtos.add(goodsDto);
        });
        return ResponseBuilder.getSuccessResponse(HttpStatus.OK, goodsDtos, goodsDtos.size(), "Goods Retrived");
//        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
//        CriteriaQuery<Goods> criteriaQuery = criteriaBuilder.createQuery(Goods.class);
//        Root<Goods> rootEntity = criteriaQuery.from(Goods.class);
//
//        addPredicates(criteriaBuilder, criteriaQuery, rootEntity, search);
//
//        TypedQuery<Goods> typedQuery = entityManager.createQuery(criteriaQuery);
//        return getAllResponse(criteriaQuery, typedQuery, pageable, isExport);
    }

    @Override
    @Transactional
    public Response getGoodsName(String description) {
        Goods goods = goodsRepository.findByDescriptionAndIsActiveTrue(description);
        if (goods != null) {
            modelMapper.getConfiguration().setPropertyCondition(Conditions.isNotNull());
            GoodsDto goodsDto = modelMapper.map(goods, GoodsDto.class);
            if (goods != null) {
                return ResponseBuilder.getSuccessResponse(HttpStatus.OK, goodsDto, "Goods found.");
            }
            return ResponseBuilder.getFailResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server error");
        }
        return ResponseBuilder.getFailResponse(HttpStatus.NOT_FOUND, "Goods not found.");
    }
}
