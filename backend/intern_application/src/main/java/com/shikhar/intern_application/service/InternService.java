package com.shikhar.intern_application.service;

import com.shikhar.intern_application.model.Intern;
import com.shikhar.intern_application.repository.InternRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service class for managing Intern business logic.
 * This layer acts as a mediator between the Controller and the Repository.
 */
@Service
public class InternService {

    private static final Logger log = LoggerFactory.getLogger(InternService.class);

    @Autowired
    private InternRepository internRepository;

    /**
     * Fetches all intern records.
     */
    public List<Intern> getAllInterns() {
        log.debug("Fetching all interns from the database");
        return internRepository.findAll();
    }

    /**
     * Persists or updates an intern. Includes manual checks for email uniqueness
     * to provide user-friendly error messages before DB constraints are hit.
     */
    public Intern saveIntern(Intern intern) {
        log.debug("Attempting to save intern: {}", intern.getEmail());

        // LOGIC FOR UPDATE
        if (intern.getId() != null) {
            log.debug("Processing update for Intern ID: {}", intern.getId());
            Intern existing = internRepository.findById(intern.getId())
                    .orElseThrow(() -> {
                        log.error("Update failed: Intern with ID {} not found", intern.getId());
                        return new RuntimeException("Intern not found");
                    });

            // If the user is trying to change their email to one already in use by someone else
            if (!existing.getEmail().equals(intern.getEmail())
                    && internRepository.existsByEmail(intern.getEmail())) {
                log.error("Update failed: Email {} is already taken by another user", intern.getEmail());
                throw new RuntimeException("Email already exists");
            }
        } 
        // LOGIC FOR NEW CREATION
        else {
            if (internRepository.existsByEmail(intern.getEmail())) {
                log.error("Creation failed: Email {} already exists", intern.getEmail());
                throw new RuntimeException("Email already exists");
            }
        }

        Intern savedIntern = internRepository.save(intern);
        log.info("Successfully saved Intern with ID: {}", savedIntern.getId());
        return savedIntern;
    }

    /**
     * Retrieves a single intern by ID.
     */
    public Intern getInternById(Long id) {
        log.debug("Finding intern by ID: {}", id);
        return internRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Search failed: Intern ID {} not found", id);
                    return new RuntimeException("Intern not found");
                });
    }

    /**
     * Deletes an intern record.
     */
    public void deleteIntern(Long id) {
        log.info("Deleting intern with ID: {}", id);
        internRepository.deleteById(id);
    }
}