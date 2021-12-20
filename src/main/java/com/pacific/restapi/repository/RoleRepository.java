package com.pacific.restapi.repository;

import com.pacific.restapi.enums.RoleName;
import com.pacific.restapi.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, /*BigInteger*/ Long> {
    Role findByNameAndIsActiveTrue(RoleName roleName);

    Role findByIdAndIsActiveTrue(Long id);
}
