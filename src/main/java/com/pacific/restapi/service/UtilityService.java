package com.pacific.restapi.service;

import com.pacific.restapi.dto.Response;
import com.pacific.restapi.model.BaseModel;
import com.pacific.restapi.model.User;
import net.sf.jasperreports.engine.JRException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.HttpEntity;

import javax.persistence.TypedQuery;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface UtilityService {
    <T extends Optional, B extends BaseModel> Response getFailResponse(T optionalT, B b);

    <B extends BaseModel, R extends JpaRepository<B, Long>> Response getCreateResponse(B b, R r);

    <B extends BaseModel> Response getNoContentResponse(B b);

    <B extends BaseModel, R extends JpaRepository<B, Long>, O extends Object> Response getUpdateResponse(B b, O o, R r);

    <O extends Object> Response getGetResponse(O o, String root);

    <B extends BaseModel, R extends JpaRepository<B, Long>> Response deleteEntityResponse(B b, R r);

    <O extends Object> Response getAllSuccessResponse(long totalRows, List<O> oList, String root);

    <B extends BaseModel> Response getAllFailureResponse(TypedQuery<B> typedQuery, boolean isExport, Pageable pageable, String root);

    <B extends BaseModel> Page<B> getAllPage(TypedQuery<B> typedQuery, Pageable pageable);

    <B extends BaseModel, R extends JpaRepository<B, Long>> Response getNullResponse(R r, Long id);

    <B extends BaseModel, R extends JpaRepository<B, Long>> B getById(R r, Long id);

    User getAuthenticatedUser();

    HttpEntity<byte[]> getPdfReport(String jasperName, Map<String, Object> params, HttpServletResponse response, String pdfName) throws SQLException, JRException, IOException;

    ;
}
