package com.shikhar.intern_application.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * The Intern entity represents the 'intern' table in the database.
 * This class uses Jakarta Persistence (JPA) for ORM mapping and 
 * Jakarta Validation for ensuring data integrity before persistence.
 */
@Entity
@Table(
        name = "intern",
        // Enforcing uniqueness at the database level provides a safety net 
        // against race conditions that application-level checks might miss.
        uniqueConstraints = @UniqueConstraint(columnNames = "email")
)
public class Intern {

    /**
     * Primary key with Identity generation strategy. 
     * The underlying DB (like MySQL or PostgreSQL) will handle the auto-increment.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The intern's full name.
     * @NotBlank ensures the string is not null and has a length > 0 after trimming.
     */
    @NotBlank(message = "Name is required")
    @Size(min = 4, message = "Name must be at least 4 characters")
    private String name;

    /**
     * The intern's professional email address.
     * Validated via regex pattern by @Email and marked as unique in the DB schema.
     */
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    @Column(unique = true)
    private String email;

    /**
     * The department the intern is assigned to (e.g., Engineering, HR).
     */
    @NotBlank(message = "Department is required")
    private String department;

    // --- Getters & Setters ---
    // Standard boilerplate for field access and modification.

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }
}