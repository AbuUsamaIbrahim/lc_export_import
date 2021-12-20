package com.pacific.restapi.service.impl;

import com.pacific.restapi.repository.RoleRepository;
import com.pacific.restapi.repository.UserRepository;
import com.pacific.restapi.service.RoleService;
import com.pacific.restapi.dto.Response;
import com.pacific.restapi.dto.RoleDto;
import com.pacific.restapi.dto.UserDto;
import com.pacific.restapi.model.Role;
import com.pacific.restapi.model.User;
import com.pacific.restapi.service.UtilityService;
import com.pacific.restapi.util.ResponseBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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

@Service("roleService")
public class RoleServiceImpl implements RoleService {
    private static final Logger logger = LogManager.getLogger(RoleServiceImpl.class.getName());
    private final UserRepository userRepository;
    private final String root = "Role";
    private final ModelMapper modelMapper;
    private final UtilityService utilityService;
    private final RoleRepository roleRepository;

    @PersistenceContext
    private EntityManager entityManager;

    public RoleServiceImpl(UserRepository userRepository, ModelMapper modelMapper, UtilityService utilityService, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
        this.utilityService = utilityService;
        this.roleRepository = roleRepository;
    }

    @Override
    @Transactional
    public Response create(RoleDto roleDto) {
        Role role = modelMapper.map(roleDto, Role.class);
        return utilityService.getCreateResponse(role, roleRepository);
    }

    @Override
    @Transactional
    public Response update(Long id, RoleDto roleDto) {
        Response notFoundFailureResponse = utilityService.getNullResponse(roleRepository, id);
        if (notFoundFailureResponse != null) {
            return notFoundFailureResponse;
        }
        try {
            Role role = utilityService.getById(roleRepository, id);
            return utilityService.getUpdateResponse(role, roleDto, roleRepository);
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
        Response notFoundFailureResponse = utilityService.getNullResponse(roleRepository, id);
        if (notFoundFailureResponse != null) {
            return notFoundFailureResponse;
        }
        try {
            Role role = utilityService.getById(roleRepository, id);
            return utilityService.deleteEntityResponse(role, roleRepository);
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
        Response notFoundFailureResponse = utilityService.getNullResponse(roleRepository, id);
        if (notFoundFailureResponse != null) {
            return notFoundFailureResponse;
        }
        try {
            Role role = utilityService.getById(roleRepository, id);
            RoleDto roleDto = modelMapper.map(role, RoleDto.class);
            return utilityService.getGetResponse(roleDto, root);
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
        CriteriaQuery<Role> criteriaQuery = criteriaBuilder.createQuery(Role.class);
        Root<Role> rootEntity = criteriaQuery.from(Role.class);

        addPredicates(criteriaBuilder, criteriaQuery, rootEntity, search);

        TypedQuery<Role> typedQuery = entityManager.createQuery(criteriaQuery);
        return getAllResponse(criteriaQuery, typedQuery, pageable, isExport);
    }

    @Override
    @Transactional
    public Response assignUsers(Long id, List<UserDto> userDtoList) {
        Response notFoundFailureResponse = utilityService.getNullResponse(roleRepository, id);
        if (notFoundFailureResponse != null) {
            return notFoundFailureResponse;
        }
        Role role = utilityService.getById(roleRepository, id);
        List<Long> userIds = new ArrayList<>();
        if (userDtoList != null && userDtoList.size() > 0) {
            userDtoList.forEach(urlDto -> {
                if (urlDto.getId() != null) {
                    userIds.add(urlDto.getId());
                }
            });
            if (userIds.size() > 0) {
                List<User> users = userRepository.findAllByIdInAndIsActiveTrue(userIds);
                role.setUsers(users);
                role = roleRepository.save(role);
                if (role != null) {
                    return ResponseBuilder.getSuccessResponse(HttpStatus.OK, null, "Users assigned successfully");
                }
                return ResponseBuilder.getFailResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Internal error occurred");
            }
            return ResponseBuilder.getFailResponse(HttpStatus.NOT_FOUND, "No user found to assign");
        }
        return ResponseBuilder.getFailResponse(HttpStatus.BAD_REQUEST, "Please provide proper user to assign");
    }

    private Response getAllResponse(CriteriaQuery<Role> criteriaQuery, TypedQuery<Role> typedQuery, Pageable pageable, boolean isExport) {
        if (utilityService.getAllFailureResponse(typedQuery, isExport, pageable, root) != null) {
            return utilityService.getAllFailureResponse(typedQuery, isExport, pageable, root);
        }
        long totalRows = this.getTotalRows(criteriaQuery);
        Page<Role> roles = utilityService.getAllPage(typedQuery, pageable);
        return utilityService.getAllSuccessResponse(totalRows, this.getResponseDtoList(roles), root);
    }

    private long getTotalRows(CriteriaQuery<Role> criteriaQuery) {
        TypedQuery<Role> typedQuery = entityManager.createQuery(criteriaQuery);
        return typedQuery.getResultList().size();
    }

    private void addPredicates(CriteriaBuilder criteriaBuilder, CriteriaQuery<Role> criteriaQuery, Root<Role> rootEntity, String search) {
        List<Predicate> predicates = new ArrayList<>();
        predicates.add(criteriaBuilder.and(criteriaBuilder.isTrue(rootEntity.<Boolean>get("active")), criteriaBuilder.equal(rootEntity.<String>get("status"), "ACTIVE")));

        if (search != null && search.trim().length() > 0) {

            Predicate pLike = criteriaBuilder.or(
                    criteriaBuilder.like(rootEntity.<String>get("name"), "%" + search + "%"));
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


    private List<RoleDto> getResponseDtoList(Page<Role> roles) {
        List<RoleDto> responseDtos = new ArrayList<>();
        roles.forEach(role -> {
            RoleDto roleDto = modelMapper.map(role, RoleDto.class);
            responseDtos.add(roleDto);
        });
        return responseDtos;
    }

}
