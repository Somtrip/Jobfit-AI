package com.jobfit.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "job_descriptions")
public class JobDescription {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private User user;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String company;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(columnDefinition = "TEXT")
    private String extractedText;

    @ElementCollection
    @CollectionTable(name = "jd_required_skills", joinColumns = @JoinColumn(name = "job_description_id"))
    @Column(name = "skill")
    private List<String> requiredSkills;

    @ElementCollection
    @CollectionTable(name = "jd_preferred_skills", joinColumns = @JoinColumn(name = "job_description_id"))
    @Column(name = "skill")
    private List<String> preferredSkills;

    @ElementCollection
    @CollectionTable(name = "jd_experience_requirements", joinColumns = @JoinColumn(name = "job_description_id"))
    @Column(name = "requirement", columnDefinition = "TEXT")
    private List<String> experienceRequirements;

    @ElementCollection
    @CollectionTable(name = "jd_education_requirements", joinColumns = @JoinColumn(name = "job_description_id"))
    @Column(name = "requirement", columnDefinition = "TEXT")
    private List<String> educationRequirements;

    private LocalDateTime createdAt;

    private LocalDateTime lastUpdated;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        lastUpdated = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        lastUpdated = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getExtractedText() {
        return extractedText;
    }

    public void setExtractedText(String extractedText) {
        this.extractedText = extractedText;
    }

    public List<String> getRequiredSkills() {
        return requiredSkills;
    }

    public void setRequiredSkills(List<String> requiredSkills) {
        this.requiredSkills = requiredSkills;
    }

    public List<String> getPreferredSkills() {
        return preferredSkills;
    }

    public void setPreferredSkills(List<String> preferredSkills) {
        this.preferredSkills = preferredSkills;
    }

    public List<String> getExperienceRequirements() {
        return experienceRequirements;
    }

    public void setExperienceRequirements(List<String> experienceRequirements) {
        this.experienceRequirements = experienceRequirements;
    }

    public List<String> getEducationRequirements() {
        return educationRequirements;
    }

    public void setEducationRequirements(List<String> educationRequirements) {
        this.educationRequirements = educationRequirements;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(LocalDateTime lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
} 