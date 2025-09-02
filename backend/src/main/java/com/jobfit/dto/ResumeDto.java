package com.jobfit.dto;

import com.jobfit.entity.Resume;
import java.time.LocalDateTime;
import java.util.List;

public class ResumeDto {
    private Long id;
    private String fileName;
    private String content;
    private String extractedText;
    private List<String> skills;
    private List<String> experience;
    private List<String> education;
    private LocalDateTime uploadedAt;
    private LocalDateTime lastUpdated;

    // Default constructor
    public ResumeDto() {
    }

    // Constructor with all fields
    public ResumeDto(Long id, String fileName, String content, String extractedText, 
                    List<String> skills, List<String> experience, List<String> education,
                    LocalDateTime uploadedAt, LocalDateTime lastUpdated) {
        this.id = id;
        this.fileName = fileName;
        this.content = content;
        this.extractedText = extractedText;
        this.skills = skills;
        this.experience = experience;
        this.education = education;
        this.uploadedAt = uploadedAt;
        this.lastUpdated = lastUpdated;
    }

    // Static factory method to create DTO from Entity
    public static ResumeDto fromEntity(Resume resume) {
        ResumeDto dto = new ResumeDto();
        dto.setId(resume.getId());
        dto.setFileName(resume.getFileName());
        dto.setContent(resume.getContent());
        dto.setExtractedText(resume.getExtractedText());
        dto.setSkills(resume.getSkills());
        dto.setExperience(resume.getExperience());
        dto.setEducation(resume.getEducation());
        dto.setUploadedAt(resume.getUploadedAt());
        dto.setLastUpdated(resume.getLastUpdated());
        return dto;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getExtractedText() {
        return extractedText;
    }

    public void setExtractedText(String extractedText) {
        this.extractedText = extractedText;
    }

    public List<String> getSkills() {
        return skills;
    }

    public void setSkills(List<String> skills) {
        this.skills = skills;
    }

    public List<String> getExperience() {
        return experience;
    }

    public void setExperience(List<String> experience) {
        this.experience = experience;
    }

    public List<String> getEducation() {
        return education;
    }

    public void setEducation(List<String> education) {
        this.education = education;
    }

    public LocalDateTime getUploadedAt() {
        return uploadedAt;
    }

    public void setUploadedAt(LocalDateTime uploadedAt) {
        this.uploadedAt = uploadedAt;
    }

    public LocalDateTime getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(LocalDateTime lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    @Override
    public String toString() {
        return "ResumeDto{" +
                "id=" + id +
                ", fileName='" + fileName + '\'' +
                ", content='" + (content != null ? content.substring(0, Math.min(50, content.length())) + "..." : "null") + '\'' +
                ", extractedText='" + (extractedText != null ? extractedText.substring(0, Math.min(50, extractedText.length())) + "..." : "null") + '\'' +
                ", skills=" + skills +
                ", experience=" + experience +
                ", education=" + education +
                ", uploadedAt=" + uploadedAt +
                ", lastUpdated=" + lastUpdated +
                '}';
    }
}