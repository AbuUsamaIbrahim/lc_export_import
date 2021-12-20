package com.pacific.restapi.service.impl;

import com.pacific.restapi.dto.BankDto;
import com.pacific.restapi.dto.BranchDto;
import com.pacific.restapi.dto.Response;
import com.pacific.restapi.model.Bank;
import com.pacific.restapi.model.Branch;
import com.pacific.restapi.repository.BankRepository;
import com.pacific.restapi.repository.BranchRepository;
import com.pacific.restapi.service.BranchService;
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

@Service("branchService")
public class BranchServiceImpl implements BranchService {
    private static final Logger logger = LogManager.getLogger(BranchServiceImpl.class.getName());
    private final BranchRepository branchRepository;
    private final BankRepository bankRepository;
    private final String root = "Lookup Data";
    private final ModelMapper modelMapper;
    private final UtilityService utilityService;

    @PersistenceContext
    private EntityManager entityManager;

    public BranchServiceImpl(BranchRepository branchRepository, BankRepository bankRepository, ModelMapper modelMapper, UtilityService utilityService) {
        this.branchRepository = branchRepository;
        this.modelMapper = modelMapper;
        this.utilityService = utilityService;
        this.bankRepository = bankRepository;
    }

    @Override
    @Transactional
    public Response create(BranchDto branchDto) {
        Branch branch = modelMapper.map(branchDto, Branch.class);
        return utilityService.getCreateResponse(branch, branchRepository);
    }

    @Override
    @Transactional
    public Response update(Long id, BranchDto branchDto) {
        Response notFoundFailureResponse = utilityService.getNullResponse(branchRepository, id);
        if (notFoundFailureResponse != null) {
            return notFoundFailureResponse;
        }
        try {
            Branch branch = utilityService.getById(branchRepository, id);
            return utilityService.getUpdateResponse(branch, branchDto, branchRepository);
        } catch (NullPointerException e) {
            logger.error(e.getMessage());
            return ResponseBuilder.getFailResponse(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            logger.error(e.getMessage());
            return ResponseBuilder.getFailResponse(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @Override
    public Response delete(Long id) {
        Response notFoundFailureResponse = utilityService.getNullResponse(branchRepository, id);
        if (notFoundFailureResponse != null) {
            return notFoundFailureResponse;
        }
        try {
            Branch branch = utilityService.getById(branchRepository, id);
            return utilityService.deleteEntityResponse(branch, branchRepository);
        } catch (NullPointerException e) {
            logger.error(e.getMessage());
            return ResponseBuilder.getFailResponse(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            logger.error(e.getMessage());
            return ResponseBuilder.getFailResponse(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @Override
    public Response get(Long id) {
        Response notFoundFailureResponse = utilityService.getNullResponse(branchRepository, id);
        if (notFoundFailureResponse != null) {
            return notFoundFailureResponse;
        }
        try {
            Branch branch = utilityService.getById(branchRepository, id);
            BranchDto branchDto = modelMapper.map(branch, BranchDto.class);
            return utilityService.getGetResponse(branchDto, root);
        } catch (NullPointerException e) {
            logger.error(e.getMessage());
            return ResponseBuilder.getFailResponse(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            logger.error(e.getMessage());
            return ResponseBuilder.getFailResponse(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @Override
    public Response getAll(Pageable pageable, boolean isExport, String search, String status) {
        List<Branch> branchList = branchRepository.findAllByIsActiveTrue();
        List<BranchDto> branchDtos = new ArrayList<>();
        branchList.forEach(branch -> {
            BranchDto branchDto = modelMapper.map(branch, BranchDto.class);
            branchDtos.add(branchDto);
        });
        return ResponseBuilder.getSuccessResponse(HttpStatus.OK, branchDtos, branchDtos.size(), "Bank retrived");
//        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
//        CriteriaQuery<Branch> criteriaQuery = criteriaBuilder.createQuery(Branch.class);
//        Root<Branch> rootEntity = criteriaQuery.from(Branch.class);
//
//        addPredicates(criteriaBuilder, criteriaQuery, rootEntity, search);
//
//        TypedQuery<Branch> typedQuery = entityManager.createQuery(criteriaQuery);
//        return getAllResponse(criteriaQuery, typedQuery, pageable, isExport);
    }

    @Override
    public Response getAllByBank(Long bankId) {
        Bank bank = bankRepository.findByIdAndIsActiveTrue(bankId);
        if (bank != null) {
            List<Branch> branchList = branchRepository.findAllByBankAndIsActiveTrue(bank);
            if (branchList != null && branchList.size() > 0) {
                List<BranchDto> branchDtoList = new ArrayList<>();
                branchList.forEach(branch -> {
                    BranchDto branchDto = modelMapper.map(branch, BranchDto.class);
                    branchDtoList.add(branchDto);
                });
                return ResponseBuilder.getSuccessResponse(HttpStatus.OK, branchDtoList, "Get Response Successful");
            }
        }
        return ResponseBuilder.getFailResponse(HttpStatus.NOT_FOUND, "Branches Not found");
    }

    @Override
    public Response getByNameAndBank(String name, Long bankId) {
        Bank bank = bankRepository.findByIdAndIsActiveTrue(bankId);
        Branch branch = branchRepository.findByNameAndBankAndIsActiveTrue(name, bank);
        if (branch != null) {
            modelMapper.getConfiguration().setPropertyCondition(Conditions.isNotNull());
            BranchDto branchDto = modelMapper.map(branch, BranchDto.class);
            if (branch != null) {
                return ResponseBuilder.getSuccessResponse(HttpStatus.OK, branchDto, "Branch found.");
            }
            return ResponseBuilder.getFailResponse(HttpStatus.INTERNAL_SERVER_ERROR, "internal server error");
        }
        return ResponseBuilder.getFailResponse(HttpStatus.NOT_FOUND, "Branch not found");
    }

    private Response getAllResponse(CriteriaQuery<Branch> criteriaQuery, TypedQuery<Branch> typedQuery, Pageable pageable, boolean isExport) {
        if (utilityService.getAllFailureResponse(typedQuery, isExport, pageable, root) != null) {
            return utilityService.getAllFailureResponse(typedQuery, isExport, pageable, root);
        }
        long totalRows = this.getTotalRows(criteriaQuery);
        Page<Branch> branchs = utilityService.getAllPage(typedQuery, pageable);
        return utilityService.getAllSuccessResponse(totalRows, this.getResponseDtoList(branchs), root);
    }

    private long getTotalRows(CriteriaQuery<Branch> criteriaQuery) {
        TypedQuery<Branch> typedQuery = entityManager.createQuery(criteriaQuery);
        return typedQuery.getResultList().size();
    }

    private void addPredicates(CriteriaBuilder criteriaBuilder, CriteriaQuery<Branch> criteriaQuery, Root<Branch> rootEntity, String search) {
        List<Predicate> predicates = new ArrayList<>();
        predicates.add(criteriaBuilder.and(criteriaBuilder.isTrue(rootEntity.<Boolean>get("isActive"))));

        if (search != null && search.trim().length() > 0) {

            Predicate pLike = criteriaBuilder.or(
                    criteriaBuilder.like(rootEntity.<String>get("name"), "%" + search + "%"),
                    criteriaBuilder.like(rootEntity.<String>get("bank"), "%" + search + "%"));
            predicates.add(pLike);
        }

        if (predicates.isEmpty()) {
            logger.error("predicates isEmpty ");
            criteriaQuery.select(rootEntity);
        } else {
            logger.error("predicates is not Empty ");
            criteriaQuery.select(rootEntity).where(predicates.toArray(new Predicate[predicates.size()]));
        }
    }


    private List<BranchDto> getResponseDtoList(Page<Branch> branchs) {
        List<BranchDto> responseDtos = new ArrayList<>();
        branchs.forEach(branch -> {
            BranchDto branchDto = modelMapper.map(branch, BranchDto.class);
            responseDtos.add(branchDto);
        });
        return responseDtos;
    }
}
