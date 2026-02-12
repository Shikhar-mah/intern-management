package com.shikhar.intern_application.controller;

import com.shikhar.intern_application.model.Intern;
import com.shikhar.intern_application.service.InternService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for managing Intern-related operations.
 * Exposes endpoints for CRUD operations and handles CORS for frontend integration.
 */
@RestController
@CrossOrigin(origins = "http://127.0.0.1:5500")
public class InternController {

    private static final Logger log = LoggerFactory.getLogger(InternController.class);

    @Autowired
    private InternService internService;

    /**
     * Retrieves a list of all interns from the database.
     * @return List of Intern entities.
     */
    @GetMapping("/interns")
    public List<Intern> getAll() {
        log.info("REST request to get all Interns");
        return internService.getAllInterns();
    }

    /**
     * Creates a new intern record.
     * @param intern The intern object validated by @Valid.
     * @return The persisted Intern entity with its generated ID.
     */
    @PostMapping("/newIntern")
    public Intern addIntern(@Valid @RequestBody Intern intern) {
        log.info("REST request to save Intern : {}", intern.getName());
        return internService.saveIntern(intern);
    }

    /**
     * Updates an existing intern record based on ID.
     * @param id The ID of the intern to update.
     * @param intern The updated intern data.
     * @return The updated Intern entity.
     */
    @PutMapping("/{id}")
    public Intern updateIntern(@PathVariable Long id, @Valid @RequestBody Intern intern) {
        log.info("REST request to update Intern ID: {} with data: {}", id, intern.getEmail());
        // Explicitly setting the ID ensures we update the existing record 
        // rather than creating a new one if the payload ID is missing.
        intern.setId(id);
        return internService.saveIntern(intern);
    }

    /**
     * Removes an intern record from the database.
     * @param id The unique identifier of the intern to be deleted.
     */
    @DeleteMapping("/eraseIntern/{id}")
    public void deleteIntern(@PathVariable Long id) {
        log.warn("REST request to delete Intern ID: {}", id);
        internService.deleteIntern(id);
    }
}