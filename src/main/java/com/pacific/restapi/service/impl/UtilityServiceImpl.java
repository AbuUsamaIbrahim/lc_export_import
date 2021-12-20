package com.pacific.restapi.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
//import com.pacific.restapi.repository.ActionLogRepository;
import com.pacific.restapi.repository.UserRepository;
import com.pacific.restapi.service.UtilityService;
import com.pacific.restapi.dto.JsonDiffDto;
import com.pacific.restapi.dto.Response;
//import com.pacific.restapi.model.ActionLog;
import com.pacific.restapi.model.BaseModel;
import com.pacific.restapi.model.User;
import com.pacific.restapi.util.DateUtils;
import com.pacific.restapi.util.JsonDiffUtil;
import com.pacific.restapi.util.ResponseBuilder;
//import com.zaxxer.hikari.HikariDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.modelmapper.Conditions;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.persistence.TypedQuery;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.*;

@Service("utilityService")
public class UtilityServiceImpl implements UtilityService {
    private static final String REPORT_DIR = "REPORT_DIR";
    private static final String IMAGE_DIR = "IMAGE_DIR";
    private static final String SUBREPORT_DIR = "SUBREPORT_DIR";
    @Value("${spring.datasource.url}")
    private String url;
    @Value("${spring.datasource.username}")
    private String username;
    @Value("${spring.datasource.password}")
    private String password;
    @Value("${spring.datasource.driver-class-name}")
    private String driverName;
    //    private final ActionLogRepository actionLogRepository;
    private final ModelMapper modelMapper;
    private final UserRepository userRepository;
    private static final Logger logger = LogManager.getLogger(UtilityServiceImpl.class.getName());


    public UtilityServiceImpl(ModelMapper modelMapper, UserRepository userRepository) {
//        this.actionLogRepository = actionLogRepository;
        this.modelMapper = modelMapper;
        this.userRepository = userRepository;
    }

    @Override
    public <T extends Optional, B extends BaseModel> Response getFailResponse(T optionalT, B b) {
        if (!optionalT.isPresent()) {
            return ResponseBuilder.getFailResponse(HttpStatus.NOT_FOUND, String.format("Requested %s could not be found", b.getClass().getSimpleName()));
        }
        if (b.getIsActive().equals(false)) {
            return ResponseBuilder.getFailResponse(HttpStatus.NOT_FOUND, String.format("Requested %s could not be found", b.getClass().getSimpleName()));
        }
        return null;
    }

    private <B extends BaseModel> Response getResponse(B b, String successMessage, String actionMessage, String oldJson) {
        if (b != null) {
//            setActionLog(b, actionMessage, oldJson);
            return ResponseBuilder.getSuccessResponse(b.getId(), null, b.getClass().getSimpleName() + " " + successMessage);
        }
        return ResponseBuilder.getFailResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Internal error occurred");
    }

    private <B extends BaseModel> void setActionLog(B b, String actionMessage, String oldJson) {
        User user = getAuthenticatedUser();
        try {
            String description = b.getClass().getSimpleName() + " " + actionMessage;
            if (oldJson != null) {
                String newJson = getJsonString(b);
                JsonDiffDto jsonDiffDto = JsonDiffUtil.getJsonDiffDto(oldJson, newJson);
                description = new ObjectMapper().writeValueAsString(jsonDiffDto);
            }
            String email = "";
            if (user != null) {
                email = user.getEmail();
            }
//            ActionLog log = new ActionLog(email, description, actionMessage, getId(b));
//            actionLogRepository.save(log);
        } catch (Exception e) {
            logger.error("exception in action log: " + e.getMessage());
        }
    }

    private String getJsonString(Object o) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
//        mapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);
        return mapper.writeValueAsString(o);
    }

    @Override
    @Transactional
    public User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        try {
            return userRepository.findByUsernameOrEmailAndIsActiveTrue(authentication.getName(), authentication.getName()).get();
        } catch (Exception e) {
            logger.error(e.getMessage());
            return null;
        }
    }

    @Override
    public HttpEntity<byte[]> getPdfReport(String jasperName, Map<String, Object> reportParams, HttpServletResponse response, String pdfName) throws SQLException, JRException, IOException {
        reportParams.put(SUBREPORT_DIR, "/home/Bayjeed/reports" + File.separator);
        reportParams.put(REPORT_DIR, "/home/Bayjeed/reports" + File.separator);
        reportParams.put(IMAGE_DIR, "/home/Bayjeed/images" + File.separator);

//        reportParams.put(SUBREPORT_DIR, "D:/Project/lc_export_import/src/main/resources/reports" + File.separator);
//        reportParams.put(REPORT_DIR, "D:/Project/lc_export_import/src/main/resources/reports" + File.separator);
//        reportParams.put(IMAGE_DIR, "D:/Project/lc_export_import/src/main/resources/static/images" + File.separator);
        try {
            Class.forName(driverName);
            Connection con = DriverManager.getConnection(url, username, password);
            JasperPrint print = JasperFillManager.fillReport("/home/Bayjeed/reports" + File.separator + jasperName + ".jasper", reportParams/*, dataSource.getConnection()*/, con);
//            JasperPrint print = JasperFillManager.fillReport("D:/Project/lc_export_import/src/main/resources/reports" + File.separator + jasperName + ".jasper", reportParams/*, dataSource.getConnection()*/, con);
            byte[] pdfBytes = JasperExportManager.exportReportToPdf(print);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            if (pdfName == null || pdfName.trim().equals("")) {
                response.setHeader("Content-Disposition", "attachment; filename=" + jasperName + "_" + DateUtils.getStringDate(new Date(), "dd-MM-yyyy") + ".pdf");
            } else {
                response.setHeader("Content-Disposition", "attachment; filename=" + pdfName + "_" + DateUtils.getStringDate(new Date(), "dd-MM-yyyy") + ".pdf");
            }
            if (!con.isClosed()) {
                con.close();
            }
            return new HttpEntity<byte[]>(pdfBytes, headers);
        } catch (ClassNotFoundException e) {
            logger.error(e.getMessage());
            return null;
        }
    }

    @Override
    @Transactional
    public <B extends BaseModel, R extends JpaRepository<B, Long>> Response getCreateResponse(B b, R r) {
        if (getAuthenticatedUser() != null) {
            b.setCreatedBy(getAuthenticatedUser().getId());
        } else {
            b.setCreatedBy(Long.valueOf("0"));
        }

        b = r.save(b);
        return getResponse(b, "created successfully", "created", null);
    }

    @Override
    @Transactional
    public <B extends BaseModel> Response getNoContentResponse(B b) {
        if (b == null) {
            return ResponseBuilder.getFailResponse(HttpStatus.NO_CONTENT, String.format("%s is not active", b.getClass().getSimpleName()));
        }
        return null;
    }

    @Override
    @Transactional
    public <B extends BaseModel, R extends JpaRepository<B, Long>, O extends Object> Response getUpdateResponse(B b, O o, R r) {
        B oldB = r.findById(getId(b)).get();
        try {
            String oldJson = getJsonString(oldB);
            modelMapper.getConfiguration().setPropertyCondition(Conditions.isNotNull());
            modelMapper.map(o, b);
            b.setUpdatedBy(getAuthenticatedUser().getId());
            b = r.save(b);
            return getResponse(b, "updated successfully", "updated", oldJson);
        } catch (JsonProcessingException e) {
            return ResponseBuilder.getFailResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error Occurs");
        }
    }


    @Override
    @Transactional
    public <O extends Object> Response getGetResponse(O o, String root) {
        if (o != null) {
            return ResponseBuilder.getSuccessResponse(HttpStatus.CREATED, o, String.format("%s retrieved successfully", root));
        }
        return ResponseBuilder.getFailResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Internal error occurred");
    }

    @Override
    @Transactional
    public <B extends BaseModel, R extends JpaRepository<B, Long>> Response deleteEntityResponse(B b, R r) {
        B oldB = r.findById(getId(b)).get();
        try {
            String oldJson = getJsonString(oldB);
            b.setIsActive(false);
            b.setUpdatedBy(getAuthenticatedUser().getId());
            b = r.save(b);
            return getResponse(b, "deleted successfully", "deleted", oldJson);
        } catch (Exception e) {
            return ResponseBuilder.getFailResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error");
        }
    }

    @Override
    public <O extends Object> Response getAllSuccessResponse(long totalRows, List<O> oList, String root) {
        return ResponseBuilder.getSuccessResponse(HttpStatus.OK, oList, totalRows, String.format("%s list", root));
    }

    @Override
    @Transactional
    public <B extends BaseModel> Response getAllFailureResponse(TypedQuery<B> typedQuery, boolean isExport, Pageable pageable, String root) {
        if (!isExport) {
            typedQuery.setFirstResult(pageable.getPageNumber() * pageable.getPageSize());
            typedQuery.setMaxResults(pageable.getPageSize());
        }
        Page<B> bs = getAllPage(typedQuery, pageable);
        if (!bs.hasContent()) {
            return ResponseBuilder.getFailResponse(HttpStatus.NOT_FOUND, String.format("%s not found", root));
        }
        return null;
    }

    @Override
    public <B extends BaseModel> Page<B> getAllPage(TypedQuery<B> typedQuery, Pageable pageable) {
        return new PageImpl<B>(typedQuery.getResultList(), pageable, getRows(typedQuery));
    }


    @Override
    @Transactional
    public <B extends BaseModel, R extends JpaRepository<B, Long>> Response getNullResponse(R r, Long id) {
        try {
            B b = getById(r, id);
            if (b == null) {
                return ResponseBuilder.getFailResponse(HttpStatus.BAD_REQUEST, getReturnType(r).getSimpleName() + " not found.");
            }
        } catch (Exception e) {
            logger.error("Method invoke access exception");
            return ResponseBuilder.getFailResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error occurs.");
        }
        return null;
    }

    @Override
    @Transactional
    public <B extends BaseModel, R extends JpaRepository<B, Long>> B getById(R r, Long id) {
        Method findByIdMethod = null;
        try {
            findByIdMethod = getFindByIdMethod(r);
        } catch (Exception e) {
            logger.error("Reflection exception");
            return null;
        }
        try {
            B b = (B) findByIdMethod.invoke(r, id);
            if (b != null) {
                return b;
            }
        } catch (IllegalAccessException e) {
            logger.error("Method invoke access exception");
            return null;
        } catch (InvocationTargetException e) {
            logger.error("Method invoke target not found exception");
            return null;
        }
        return null;
    }


    private <B extends BaseModel, R extends JpaRepository<B, Long>> Class getReturnType(R r, String methodName) {
        Class returnType = null;
        try {
            Method findByIdMethod = getFindByIdMethod(r, methodName);
            returnType = findByIdMethod.getReturnType();
            return returnType;
        } catch (Exception e) {
            logger.error("Reflection exception");
            return null;
        }
    }

    private <B extends BaseModel, R extends JpaRepository<B, Long>> Class getReturnType(R r) {
        return getReturnType(r, "findByIdAndIsActiveTrue");
    }

    private <B extends BaseModel> Long getId(B b) {
        Method getIdMethod = null;
        try {
            getIdMethod = getMethod(b, "getId");
        } catch (Exception e) {
            logger.error("Reflection exception");
            return null;
        }
        try {
            Long id = (Long) getIdMethod.invoke(b);
            if (id != null) {
                return id;
            }
        } catch (IllegalAccessException e) {
            logger.error("Method invoke access exception");
            return null;
        } catch (InvocationTargetException e) {
            logger.error("Method invoke target not found exception");
            return null;
        }
        return null;
    }

    private <B extends BaseModel, R extends JpaRepository<B, Long>> Method getFindByIdMethod(R r, String methodName) {
        return getMethod(r.getClass().getName(), methodName, Long.class);
    }

    private <B extends BaseModel> Method getMethod(B b, String methodName) {
        return getMethod(b.getClass().getName(), methodName);
    }

    private Method getMethod(String registerClassName, String methodName, Class... parameters) {
        Class className = null;
        Method method = null;
        try {
            className = Class.forName(registerClassName);
            method = className.getMethod(methodName, parameters);
            method.setAccessible(true);
            return method;
        } catch (Exception e) {
            logger.error("Reflection exception");
            return null;
        }
    }

    private <B extends BaseModel, R extends JpaRepository<B, Long>> Method getFindByIdMethod(R r) {
        return getFindByIdMethod(r, "findByIdAndIsActiveTrue");
    }

    private <B extends BaseModel> long getRows(TypedQuery<B> typedQuery) {
        return typedQuery.getResultList().size();
    }


}
