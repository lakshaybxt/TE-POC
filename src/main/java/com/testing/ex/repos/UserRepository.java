package com.testing.ex.repos;

import brave.internal.collect.UnsafeArrayMap;
import com.testing.ex.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repository for User persistence operations.
 */
public interface UserRepository extends JpaRepository<User, Long> {
    /**
     * Check if an email is already registered.
     */
    boolean existsByEmail(String email);
    /**
     * Check if a username is already taken.
     */
    boolean existsByUsername(String username);
    /**
     * Find a user by email.
     */
    Optional<User> findByEmail(String email);
}
