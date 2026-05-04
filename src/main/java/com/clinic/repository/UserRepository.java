package com.clinic.repository;

import com.clinic.model.User;
import com.clinic.model.enums.Role;
import com.clinic.model.enums.UserStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    List<User> findByRoleAndStatus(Role role, UserStatus status);
}