package com.shikhar.intern_application.repository;

import com.shikhar.intern_application.model.Intern;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Data Access Layer for Intern entity.
 * JpaRepository provides standard CRUD methods (save, findAll, deleteById, etc.) 
 * without requiring manual implementation.
 */
@Repository
public interface InternRepository extends JpaRepository<Intern, Long> {

    /**
     * Derived query to find an intern by their email address.
     * Spring Data JPA parses the method name to generate the SQL query.
     */
    Optional<Intern> findByEmail(String email);

    /**
     * Efficiently checks if a record exists with the given email.
     * @return true if email exists, false otherwise.
     */
    boolean existsByEmail(String email);
}