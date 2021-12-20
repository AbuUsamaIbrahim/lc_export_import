package com.pacific.restapi.repository;

import com.pacific.restapi.model.ActionLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by Mozahid on 11/1/17.
 */
@Repository
public interface ActionLogRepository extends JpaRepository<ActionLog, Long> {

}
