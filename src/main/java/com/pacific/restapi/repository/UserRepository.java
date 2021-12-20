package com.pacific.restapi.repository;

import com.pacific.restapi.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, /*BigInteger*/Long> {
    User findByIdAndIsActiveTrue(Long id);

    User findByEmailAndIsActiveTrue(String email);

    Optional<User> findByUsernameOrEmailAndIsActiveTrue(String username, String email);

    List<User> findAllByEmailAndIsActiveTrue(String email);

    List<User> findAllByIdInAndIsActiveTrue(List<Long> userIds);
}
