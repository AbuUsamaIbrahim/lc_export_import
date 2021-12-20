package com.pacific.restapi.service.impl;

import com.pacific.restapi.dto.BankBranchDto;
import com.pacific.restapi.dto.BankDto;
import com.pacific.restapi.dto.Response;
import com.pacific.restapi.model.Bank;
import com.pacific.restapi.model.Branch;
import com.pacific.restapi.repository.BankRepository;
import com.pacific.restapi.repository.BranchRepository;
import com.pacific.restapi.service.BankService;
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

@Service("bankService")
public class BankServiceImpl implements BankService {
    private static final Logger logger = LogManager.getLogger(BankServiceImpl.class.getName());
    private final BankRepository bankRepository;
    private final BranchRepository branchRepository;
    private final String root = "Bank";
    private final ModelMapper modelMapper;
    private final UtilityService utilityService;

    @PersistenceContext
    private EntityManager entityManager;

    public BankServiceImpl(BankRepository bankRepository, ModelMapper modelMapper, UtilityService utilityService, BranchRepository branchRepository) {
        this.bankRepository = bankRepository;
        this.modelMapper = modelMapper;
        this.utilityService = utilityService;
        this.branchRepository = branchRepository;
    }

    @Override
    @Transactional
    public Response create(BankDto bankDto) {
        Bank bank = modelMapper.map(bankDto, Bank.class);
        return utilityService.getCreateResponse(bank, bankRepository);
    }

    @Override
    @Transactional
    public Response update(Long id, BankDto bankDto) {
        Response notFoundFailureResponse = utilityService.getNullResponse(bankRepository, id);
        if (notFoundFailureResponse != null) {
            return notFoundFailureResponse;
        }
        try {
            Bank bank = utilityService.getById(bankRepository, id);
            return utilityService.getUpdateResponse(bank, bankDto, bankRepository);
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
        Response notFoundFailureResponse = utilityService.getNullResponse(bankRepository, id);
        if (notFoundFailureResponse != null) {
            return notFoundFailureResponse;
        }
        try {
            Bank bank = utilityService.getById(bankRepository, id);
            return utilityService.deleteEntityResponse(bank, bankRepository);
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
        Response notFoundFailureResponse = utilityService.getNullResponse(bankRepository, id);
        if (notFoundFailureResponse != null) {
            return notFoundFailureResponse;
        }
        try {
            Bank bank = utilityService.getById(bankRepository, id);
            BankDto bankDto = modelMapper.map(bank, BankDto.class);
            return utilityService.getGetResponse(bankDto, root);
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
        List<Bank> bankList = bankRepository.findAllByIsActiveTrue();
        List<BankDto> bankDtos = new ArrayList<>();
        bankList.forEach(bank -> {
            BankDto bankDto = modelMapper.map(bank, BankDto.class);
            bankDtos.add(bankDto);
        });
        return ResponseBuilder.getSuccessResponse(HttpStatus.OK, bankDtos, bankDtos.size(), "Bank retrived");
//        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
//        CriteriaQuery<Bank> criteriaQuery = criteriaBuilder.createQuery(Bank.class);
//        Root<Bank> rootEntity = criteriaQuery.from(Bank.class);
//
//        addPredicates(criteriaBuilder, criteriaQuery, rootEntity, search);
//
//        TypedQuery<Bank> typedQuery = entityManager.createQuery(criteriaQuery);
//        return getAllResponse(criteriaQuery, typedQuery, pageable, isExport);
    }

    @Override
    @Transactional
    public Response getAllBankBranch() {
        List<BankBranchDto> bankBranchDtos = new ArrayList<>();
        List<Bank> bankList = bankRepository.findAllByIsActiveTrue();
        bankList.forEach(bank -> {
            List<Branch> branchList = branchRepository.findAllByBankAndIsActiveTrue(bank);
            branchList.forEach(branch -> {
                BankBranchDto dto = new BankBranchDto();
                dto.setBankName(bank.getName());
                dto.setId(bank.getId());
                dto.setBranchName(branch.getName());
                dto.setBankBranchName(bank.getName() + " ( " + branch.getName() + " )");
                bankBranchDtos.add(dto);
            });
        });
        if (bankBranchDtos != null && bankBranchDtos.size() > 0) {
            return ResponseBuilder.getSuccessResponse(HttpStatus.OK, bankBranchDtos, "Bank Branch Retrieve successfully");
        }
        return ResponseBuilder.getFailResponse(HttpStatus.NOT_FOUND, "Data not Found");
    }

    @Override
    @Transactional
    public Response getName(String name) {
        Bank bank = bankRepository.findByNameAndIsActiveTrue(name);
        if (bank != null) {
            modelMapper.getConfiguration().setPropertyCondition(Conditions.isNotNull());
            BankDto bankDto = modelMapper.map(bank, BankDto.class);
            if (bank != null) {
                return ResponseBuilder.getSuccessResponse(HttpStatus.OK, bankDto, "Bank Found");
            }
            return ResponseBuilder.getFailResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error");
        }
        return ResponseBuilder.getFailResponse(HttpStatus.NOT_FOUND, "Bank not found.");

    }
}
