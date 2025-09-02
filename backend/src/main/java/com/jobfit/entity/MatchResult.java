package com.jobfit.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Entity
@Table(name = "match_results")
public class MatchResult {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resume_id", nullable = false)
    private Resume resume;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_description_id", nullable = false)
    private JobDescription jobDescription;

    @Column(nullable = false)
    private Double overallScore;

    @Column(nullable = false)
    private Double skillsScore;

    @Column(nullable = false)
    private Double experienceScore;

    @Column(nullable = false)
    private Double educationScore;

    @ElementCollection
    @CollectionTable(name = "match_missing_skills", joinColumns = @JoinColumn(name = "match_result_id"))
    @Column(name = "skill")
    private List<String> missingSkills;

    @ElementCollection
    @CollectionTable(name = "match_skill_scores", joinColumns = @JoinColumn(name = "match_result_id"))
    @MapKeyColumn(name = "skill")
    @Column(name = "score")
    private Map<String, Double> skillScores;

    @ElementCollection
    @CollectionTable(name = "match_improvement_suggestions", joinColumns = @JoinColumn(name = "match_result_id"))
    @Column(name = "suggestion", columnDefinition = "TEXT")
    private List<String> improvementSuggestions;

    @ElementCollection
    @CollectionTable(name = "match_learning_resources", joinColumns = @JoinColumn(name = "match_result_id"))
    @Column(name = "resource", columnDefinition = "TEXT")
    private List<String> learningResources;

    private LocalDateTime createdAt;

    @Column(nullable = false)
    private Double semanticSimilarity;

    

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Resume getResume() {
        return resume;
    }

    public void setResume(Resume resume) {
        this.resume = resume;
    }

    public JobDescription getJobDescription() {
        return jobDescription;
    }

    public void setJobDescription(JobDescription jobDescription) {
        this.jobDescription = jobDescription;
    }

    public Double getOverallScore() {
        return overallScore;
    }

    public void setOverallScore(Double overallScore) {
        this.overallScore = overallScore;
    }

    public Double getSkillsScore() {
        return skillsScore;
    }

    public void setSkillsScore(Double skillsScore) {
        this.skillsScore = skillsScore;
    }

    public Double getExperienceScore() {
        return experienceScore;
    }

    public void setExperienceScore(Double experienceScore) {
        this.experienceScore = experienceScore;
    }

    public Double getEducationScore() {
        return educationScore;
    }

    public void setEducationScore(Double educationScore) {
        this.educationScore = educationScore;
    }

    public List<String> getMissingSkills() {
        return missingSkills;
    }

    public void setMissingSkills(List<String> missingSkills) {
        this.missingSkills = missingSkills;
    }

    public Map<String, Double> getSkillScores() {
        return skillScores;
    }

    public void setSkillScores(Map<String, Double> skillScores) {
        this.skillScores = skillScores;
    }

    public List<String> getImprovementSuggestions() {
        return improvementSuggestions;
    }

    public void setImprovementSuggestions(List<String> improvementSuggestions) {
        this.improvementSuggestions = improvementSuggestions;
    }

    public List<String> getLearningResources() {
        return learningResources;
    }

    public void setLearningResources(List<String> learningResources) {
        this.learningResources = learningResources;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Double getSemanticSimilarity() {
        return semanticSimilarity;
    }

    public void setSemanticSimilarity(Double semanticSimilarity) {
        this.semanticSimilarity = semanticSimilarity;
    }
}