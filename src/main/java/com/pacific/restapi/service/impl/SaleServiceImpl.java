package com.pacific.restapi.service.impl;

import com.pacific.restapi.dto.GoodsDto;
import com.pacific.restapi.dto.SaleDto;
import com.pacific.restapi.dto.Response;
import com.pacific.restapi.model.Goods;
import com.pacific.restapi.model.Sale;
import com.pacific.restapi.model.SalesPriceHistory;
import com.pacific.restapi.repository.*;
import com.pacific.restapi.service.SaleService;
import com.pacific.restapi.service.UtilityService;
import com.pacific.restapi.util.ResponseBuilder;
import net.sf.jasperreports.engine.JRException;
import org.apache.commons.math3.util.Precision;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.modelmapper.Conditions;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.pacific.restapi.util.NumberToWordConverter.getMoneyIntoWords;


@Service("saleService")
public class SaleServiceImpl implements SaleService {
    private static final Logger logger = LogManager.getLogger(SaleServiceImpl.class.getName());
    private final SaleRepository saleRepository;
    private final String root = "Sale";
    private final ModelMapper modelMapper;
    private final UtilityService utilityService;
    private final CustomerRepository customerRepository;
    private final BankRepository bankRepository;
    private final GoodsRepository goodsRepository;
    private final SalesPriceHistoryRepository salesPriceHistoryRepository;

    @PersistenceContext
    private EntityManager entityManager;

    public SaleServiceImpl(SaleRepository saleRepository, ModelMapper modelMapper, UtilityService utilityService, CustomerRepository customerRepository, BankRepository bankRepository, GoodsRepository goodsRepository, SalesPriceHistoryRepository salesPriceHistoryRepository) {
        this.saleRepository = saleRepository;
        this.modelMapper = modelMapper;
        this.utilityService = utilityService;
        this.customerRepository = customerRepository;
        this.bankRepository = bankRepository;
        this.goodsRepository = goodsRepository;
        this.salesPriceHistoryRepository = salesPriceHistoryRepository;
    }

    @Override
    @Transactional
    public Response create(SaleDto saleDto) {
        Sale sale = modelMapper.map(saleDto, Sale.class);
        sale.setCustomer(customerRepository.findByIdAndIsActiveTrue(sale.getCustomer().getId()));
        sale.setAdvisingBank(bankRepository.findByIdAndIsActiveTrue(sale.getAdvisingBank().getId()));
        sale.setLcBank(bankRepository.findByIdAndIsActiveTrue(sale.getLcBank().getId()));
        List<Goods> goodsList = new ArrayList<>();
        List<Goods> goodsNewList = new ArrayList<>();
        if (sale.getGoodsList() == null || sale.getGoodsList().size() == 0) {
            return ResponseBuilder.getFailResponse(HttpStatus.NOT_FOUND, "Goods not found.");
        }
        sale.getGoodsList().forEach(goods -> {
            goodsNewList.add(goods);
            Goods goodsOld = goodsRepository.findByIdAndIsActiveTrue(goods.getId());
            goodsList.add(goodsOld);
        });
        sale.setGoodsList(goodsList);
        sale = saleRepository.save(sale);
        if (sale != null && goodsNewList.size() > 0) {
            saveAllHistory(goodsNewList, sale.getId());
            return ResponseBuilder.getSuccessResponse(HttpStatus.CREATED, null, root + " created successfully");
        }
        return utilityService.getNoContentResponse(sale);
    }

    @Override
    @Transactional
    public Response update(Long id, SaleDto saleDto) {
        Response notFoundFailureResponse = utilityService.getNullResponse(saleRepository, id);
        if (notFoundFailureResponse != null) {
            return notFoundFailureResponse;
        }
        try {
            Sale sale = utilityService.getById(saleRepository, id);
            sale.setGoodsList(null);
            List<SalesPriceHistory> salesPriceHistoryList = salesPriceHistoryRepository.findAllBySalesIdAndIsActiveTrue(sale.getId());
            salesPriceHistoryList.forEach(salesPriceHistory -> {
                salesPriceHistory.setIsActive(false);
                salesPriceHistory = salesPriceHistoryRepository.save(salesPriceHistory);
            });
            modelMapper.getConfiguration().setPropertyCondition(Conditions.isNotNull());
            modelMapper.map(saleDto, sale);
            sale.setUpdatedBy(utilityService.getAuthenticatedUser().getId());
            sale.setCustomer(customerRepository.findByIdAndIsActiveTrue(sale.getCustomer().getId()));
            sale.setAdvisingBank(bankRepository.findByIdAndIsActiveTrue(sale.getAdvisingBank().getId()));
            sale.setLcBank(bankRepository.findByIdAndIsActiveTrue(sale.getLcBank().getId()));
            List<Goods> goodsList = new ArrayList<>();
            saleDto.getGoodsList().forEach(goodsDto -> {
                Goods goods = modelMapper.map(goodsDto, Goods.class);
                goods = goodsRepository.save(goods);
                goodsList.add(goods);
            });

            sale.setGoodsList(goodsList);
            sale = saleRepository.save(sale);
            if (sale != null && goodsList.size() > 0) {
                saveAllHistory(goodsList, sale.getId());
                return ResponseBuilder.getSuccessResponse(HttpStatus.OK, null, root + " updates successfully");
            }
        } catch (NullPointerException e) {
            logger.error(e.getMessage());
            return ResponseBuilder.getFailResponse(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            logger.error(e.getMessage());
            return ResponseBuilder.getFailResponse(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
        return ResponseBuilder.getFailResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error");
    }

    private void saveAllHistory(List<Goods> goodsList, Long salesId) {
        goodsList.forEach(goods -> {
            SalesPriceHistory history = new SalesPriceHistory();
            history.setGoodsId(goods.getId());
            history.setSalesId(salesId);
            history.setTotalQuantity(goods.getQuantity());
            history.setUnitPrice(goods.getUnitPrice());
            history.setUnit(goods.getUnit());
            history.setPackOrMarks(goods.getPackOrMarks());
            history.setGoodsDesc(goods.getDescription());
            history = salesPriceHistoryRepository.save(history);
        });
    }


    @Override
    public Response delete(Long id) {
        Response notFoundFailureResponse = utilityService.getNullResponse(saleRepository, id);
        if (notFoundFailureResponse != null) {
            return notFoundFailureResponse;
        }
        try {
            Sale sale = utilityService.getById(saleRepository, id);
            return utilityService.deleteEntityResponse(sale, saleRepository);
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
        Response notFoundFailureResponse = utilityService.getNullResponse(saleRepository, id);
        if (notFoundFailureResponse != null) {
            return notFoundFailureResponse;
        }
        try {
            Sale sale = utilityService.getById(saleRepository, id);
            SaleDto saleDto = modelMapper.map(sale, SaleDto.class);
            List<SalesPriceHistory> salesPriceHistoryList = salesPriceHistoryRepository.findAllBySalesIdAndIsActiveTrue(sale.getId());
            saleDto.setGoodsList(null);
            List<GoodsDto> goodsDtos = new ArrayList<>();
            salesPriceHistoryList.forEach(salesPriceHistory -> {
                GoodsDto goodsDto = new GoodsDto();
                goodsDto.setId(salesPriceHistory.getGoodsId());
                goodsDto.setDescription(salesPriceHistory.getGoodsDesc());
                goodsDto.setPackOrMarks(salesPriceHistory.getPackOrMarks());
                goodsDto.setQuantity(salesPriceHistory.getTotalQuantity());
                goodsDto.setUnit(salesPriceHistory.getUnit());
                goodsDto.setUnitPrice(salesPriceHistory.getUnitPrice());
                goodsDtos.add(goodsDto);
            });
            saleDto.setGoodsList(goodsDtos);
            return utilityService.getGetResponse(saleDto, root);
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
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Sale> criteriaQuery = criteriaBuilder.createQuery(Sale.class);
        Root<Sale> rootEntity = criteriaQuery.from(Sale.class);

        addPredicates(criteriaBuilder, criteriaQuery, rootEntity, search);

        TypedQuery<Sale> typedQuery = entityManager.createQuery(criteriaQuery);
        return getAllResponse(criteriaQuery, typedQuery, pageable, isExport);
    }

    @Override
    @Transactional
    public Response getbyPerformaInvoice(String performaInvoice) {
        Sale sale = saleRepository.findByProformaInvoiceNoAndIsActiveTrue(performaInvoice);
        if (sale != null) {
            modelMapper.getConfiguration().setPropertyCondition(Conditions.isNotNull());
            SaleDto saleDto = modelMapper.map(sale, SaleDto.class);
            if (sale != null) {
                return ResponseBuilder.getSuccessResponse(HttpStatus.OK, saleDto, "Found Data.");
            }
            return ResponseBuilder.getFailResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error");
        }
        return ResponseBuilder.getFailResponse(HttpStatus.NOT_FOUND, "Data not found.");
    }

    @Override
    public HttpEntity<byte[]> getReportsByParams(Long sales_id, String bank, String branch, String address, String account, HttpServletResponse response) {
        Map<String, Object> reportParams = new HashMap<>();
        String jasperName = "application";
        Double totalAmount = (findTotalAmount(sales_id));
        String amount = getMoneyIntoWords(totalAmount);
        BigDecimal d = new BigDecimal(String.valueOf(totalAmount));
        reportParams.put("sales_id", sales_id);
        reportParams.put("amount", d.toPlainString());
        reportParams.put("amountInWords", amount);
        reportParams.put("bank", bank);
        reportParams.put("branch", branch);
        reportParams.put("address", address);
        reportParams.put("account", account);

        try {
            return utilityService.getPdfReport(jasperName, reportParams, response, "Application" + "_of" + sales_id);
        } catch (SQLException | JRException | IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Double findTotalAmount(Long id) {
        List<SalesPriceHistory> salesList = salesPriceHistoryRepository.findAllBySalesIdAndIsActiveTrue(id);
        final double[] total = {0};
        salesList.forEach(salesPriceHistory -> {
            total[0] = total[0] + (salesPriceHistory.getTotalQuantity() * salesPriceHistory.getUnitPrice());
        });
        return Precision.round(total[0], 2);
    }


    @Override
    public HttpEntity<byte[]> getAllReportsByType(Long id, String type, HttpServletResponse response) {
        Map<String, Object> reportParams = new HashMap<>();
        String jasperName = "";
        Double totalAmount = (findTotalAmount(id));
        String amount = getMoneyIntoWords(totalAmount);
        BigDecimal d = new BigDecimal(String.valueOf(totalAmount));
        reportParams.put("sales_id", id);
        reportParams.put("amount", d.toPlainString());
        reportParams.put("amountInWords", amount);
        if (type.equals("truckChalan")) {
            jasperName = "lc";
        }
        if (type.equals("delivery")) {
            jasperName = "lc - Copy";
        }
        if (type.equals("packingList")) {
            jasperName = "packingList";
        }
        if (type.equals("commercialInvoice")) {
            jasperName = "commercila_Invoice";
        }
        if (type.equals("performaInvoice")) {
            jasperName = "performa_invoice";
        }
        if (type.equals("billOfExchange")) {
            jasperName = "bill_of_exchange";
        }
        if (type.equals("certificateOfOrigin")) {
            jasperName = "certificate_origin";
        }
        if (type.equals("certificateOfBenefi")) {
            jasperName = "certificate_beneficiary";
        }
        try {
            return utilityService.getPdfReport(jasperName, reportParams, response, type + "_of" + id);
        } catch (SQLException | JRException | IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Response getAllResponse(CriteriaQuery<Sale> criteriaQuery, TypedQuery<Sale> typedQuery, Pageable pageable, boolean isExport) {
        if (utilityService.getAllFailureResponse(typedQuery, isExport, pageable, root) != null) {
            return utilityService.getAllFailureResponse(typedQuery, isExport, pageable, root);
        }
        long totalRows = this.getTotalRows(criteriaQuery);
        Page<Sale> sales = utilityService.getAllPage(typedQuery, pageable);
        return utilityService.getAllSuccessResponse(totalRows, this.getResponseDtoList(sales), root);
    }

    private long getTotalRows(CriteriaQuery<Sale> criteriaQuery) {
        TypedQuery<Sale> typedQuery = entityManager.createQuery(criteriaQuery);
        return typedQuery.getResultList().size();
    }

    private void addPredicates(CriteriaBuilder criteriaBuilder, CriteriaQuery<Sale> criteriaQuery, Root<Sale> rootEntity, String search) {
        List<Predicate> predicates = new ArrayList<>();
        predicates.add(criteriaBuilder.and(criteriaBuilder.isTrue(rootEntity.<Boolean>get("isActive"))));

        if (search != null && search.trim().length() > 0) {

            Predicate pLike = criteriaBuilder.or(
                    criteriaBuilder.like(rootEntity.<String>get("invoiceNo"), "%" + search + "%"),
                    criteriaBuilder.like(rootEntity.<String>get("proformaInvoiceNo"), "%" + search + "%"),
                    criteriaBuilder.like(rootEntity.<String>get("lcNo"), "%" + search + "%"));
            predicates.add(pLike);
        }

        if (predicates.isEmpty()) {
            logger.error("predicates isEmpty ");
            criteriaQuery.select(rootEntity).orderBy(criteriaBuilder.desc(rootEntity.get("createdAt")));
        } else {
            logger.error("predicates is not Empty ");
            criteriaQuery.select(rootEntity).where(predicates.toArray(new Predicate[predicates.size()])).orderBy(criteriaBuilder.desc(rootEntity.get("createdAt")));
        }
    }


    private List<SaleDto> getResponseDtoList(Page<Sale> sales) {
        List<SaleDto> responseDtos = new ArrayList<>();
        sales.forEach(sale -> {
            SaleDto saleDto = modelMapper.map(sale, SaleDto.class);
            responseDtos.add(saleDto);
        });
        return responseDtos;
    }
}
