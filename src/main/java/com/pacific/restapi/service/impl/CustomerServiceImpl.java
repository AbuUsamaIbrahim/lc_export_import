package com.pacific.restapi.service.impl;

import com.pacific.restapi.dto.CustomerDto;
import com.pacific.restapi.dto.Response;
import com.pacific.restapi.model.Customer;
import com.pacific.restapi.repository.CustomerRepository;
import com.pacific.restapi.service.CustomerService;
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

@Service("customerService")
public class CustomerServiceImpl implements CustomerService {
    private static final Logger logger = LogManager.getLogger(CustomerServiceImpl.class.getName());
    private final CustomerRepository customerRepository;
    private final String root = "Lookup Data";
    private final ModelMapper modelMapper;
    private final UtilityService utilityService;

    @PersistenceContext
    private EntityManager entityManager;

    public CustomerServiceImpl(CustomerRepository customerRepository, ModelMapper modelMapper, UtilityService utilityService) {
        this.customerRepository = customerRepository;
        this.modelMapper = modelMapper;
        this.utilityService = utilityService;
    }

    @Override
    @Transactional
    public Response create(CustomerDto customerDto) {
        Customer customer = modelMapper.map(customerDto, Customer.class);
        return utilityService.getCreateResponse(customer, customerRepository);
    }

    @Override
    @Transactional
    public Response update(Long id, CustomerDto customerDto) {
        Response notFoundFailureResponse = utilityService.getNullResponse(customerRepository, id);
        if (notFoundFailureResponse != null) {
            return notFoundFailureResponse;
        }
        try {
            Customer customer = utilityService.getById(customerRepository, id);
            return utilityService.getUpdateResponse(customer, customerDto, customerRepository);
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
        Response notFoundFailureResponse = utilityService.getNullResponse(customerRepository, id);
        if (notFoundFailureResponse != null) {
            return notFoundFailureResponse;
        }
        try {
            Customer customer = utilityService.getById(customerRepository, id);
            return utilityService.deleteEntityResponse(customer, customerRepository);
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
        Response notFoundFailureResponse = utilityService.getNullResponse(customerRepository, id);
        if (notFoundFailureResponse != null) {
            return notFoundFailureResponse;
        }
        try {
            Customer customer = utilityService.getById(customerRepository, id);
            CustomerDto customerDto = modelMapper.map(customer, CustomerDto.class);
            return utilityService.getGetResponse(customerDto, root);
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
        List<Customer> customers = customerRepository.findAllByIsActiveTrue();
        List<CustomerDto> customerDtos = new ArrayList<>();
        customers.forEach(customer -> {
            CustomerDto dto = modelMapper.map(customer, CustomerDto.class);
            customerDtos.add(dto);
        });
        return ResponseBuilder.getSuccessResponse(HttpStatus.OK, customerDtos, customerDtos.size(), "Customer Retrived");

//        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
//        CriteriaQuery<Customer> criteriaQuery = criteriaBuilder.createQuery(Customer.class);
//        Root<Customer> rootEntity = criteriaQuery.from(Customer.class);
//
//        addPredicates(criteriaBuilder, criteriaQuery, rootEntity, search);
//
//        TypedQuery<Customer> typedQuery = entityManager.createQuery(criteriaQuery);
//        return getAllResponse(criteriaQuery, typedQuery, pageable, isExport);
    }

    @Override
    public Response getName(String name, String address) {
        Customer customer = customerRepository.findByNameAndAddressAndIsActiveTrue(name, address);
        if (customer != null) {
            modelMapper.getConfiguration().setPropertyCondition(Conditions.isNotNull());
            CustomerDto customerDto = modelMapper.map(customer, CustomerDto.class);
            if (customer != null) {
                return ResponseBuilder.getSuccessResponse(HttpStatus.OK, customerDto, "Customer found.");
            }
            return ResponseBuilder.getFailResponse(HttpStatus.INTERNAL_SERVER_ERROR, "internal server error");
        }
        return ResponseBuilder.getFailResponse(HttpStatus.NOT_FOUND, "Customer not found.");
    }
}
