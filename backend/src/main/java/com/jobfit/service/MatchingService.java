package com.jobfit.service;

import com.jobfit.entity.*;
import com.jobfit.repository.MatchResultRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class MatchingService {
    @Autowired
    private MatchResultRepository matchResultRepository;

    @Autowired
    private HuggingFaceService huggingFaceService;

    @Value("${huggingface.api.enabled:true}")
    private boolean huggingFaceEnabled;

     @Autowired
    private RestTemplate restTemplate;

    // private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public MatchResult matchResumeToJob(Resume resume, JobDescription jobDescription) {

        Optional<MatchResult> existing = matchResultRepository.findByResumeIdAndJobDescriptionId(
        resume.getId(), jobDescription.getId()
    );
        // Calculate similarity scores
        double skillsScore = calculateSkillsSimilarity(resume.getSkills(), jobDescription.getRequiredSkills());
        double experienceScore = calculateExperienceSimilarity(resume.getExperience(), jobDescription.getExperienceRequirements());
        double educationScore = calculateEducationSimilarity(resume.getEducation(), jobDescription.getEducationRequirements());
        
        // Calculate semantic similarity if Hugging Face is enabled
        double semanticSimilarity = 0.0;
        if (huggingFaceEnabled && resume.getExtractedText() != null && jobDescription.getDescription() != null) {
            try {
                semanticSimilarity = huggingFaceService.calculateSemanticSimilarity(
                    resume.getExtractedText(), 
                    jobDescription.getDescription()
                );
            } catch (Exception e) {
                System.err.println("Hugging Face API call failed: " + e.getMessage());
                // Fallback to traditional scoring
                semanticSimilarity = (skillsScore + experienceScore + educationScore) / 3;
            }
        } else {
            // Use traditional scoring if Hugging Face is disabled
            semanticSimilarity = (skillsScore + experienceScore + educationScore) / 3;
        }
        
        // Calculate overall score (weighted average)
        // Adjust weights based on the importance of each factor
        double overallScore;
        if (huggingFaceEnabled) {
            // Give more weight to semantic similarity when using AI
            overallScore = (semanticSimilarity * 0.4) + 
                          (skillsScore * 0.3) + 
                          (experienceScore * 0.2) + 
                          (educationScore * 0.1);
        } else {
            // Traditional weights without AI
            overallScore = (skillsScore * 0.5) + (experienceScore * 0.3) + (educationScore * 0.2);
        }
        
        // Find missing skills
        List<String> missingSkills = findMissingSkills(resume.getSkills(), jobDescription.getRequiredSkills());
        
        // Generate improvement suggestions
        List<String> suggestions = generateImprovementSuggestions(missingSkills, skillsScore, overallScore);
        
        // Generate learning resources
        List<String> learningResources = generateLearningResources(missingSkills);
        
        // Create skill scores map
        Map<String, Double> skillScores = calculateIndividualSkillScores(resume.getSkills(), jobDescription.getRequiredSkills());
        
        // Create and save match result
        // MatchResult matchResult = new MatchResult();
        MatchResult matchResult = existing.orElse(new MatchResult());
        matchResult.setResume(resume);
        matchResult.setJobDescription(jobDescription);
        matchResult.setOverallScore(overallScore);
        matchResult.setSkillsScore(skillsScore);
        matchResult.setExperienceScore(experienceScore);
        matchResult.setEducationScore(educationScore);
        matchResult.setSemanticSimilarity(semanticSimilarity);
        matchResult.setMissingSkills(missingSkills);
        matchResult.setSkillScores(skillScores);
        matchResult.setImprovementSuggestions(suggestions);
        matchResult.setLearningResources(learningResources);
        
        return matchResultRepository.save(matchResult);
    }

    private double calculateSkillsSimilarity(List<String> resumeSkills, List<String> requiredSkills) {
        if (requiredSkills == null || requiredSkills.isEmpty()) {
            return 1.0;
        }
        
        if (resumeSkills == null || resumeSkills.isEmpty()) {
            return 0.0;
        }
        
        // Convert to lowercase for comparison
        Set<String> resumeSkillsLower = resumeSkills.stream()
                .map(String::toLowerCase)
                .collect(Collectors.toSet());
        
        Set<String> requiredSkillsLower = requiredSkills.stream()
                .map(String::toLowerCase)
                .collect(Collectors.toSet());
        
        // Calculate Jaccard similarity
        Set<String> intersection = new HashSet<>(resumeSkillsLower);
        intersection.retainAll(requiredSkillsLower);
        
        Set<String> union = new HashSet<>(resumeSkillsLower);
        union.addAll(requiredSkillsLower);
        
        return union.isEmpty() ? 0.0 : (double) intersection.size() / union.size();
    }

    private double calculateExperienceSimilarity(List<String> resumeExperience, List<String> requiredExperience) {
        if (requiredExperience == null || requiredExperience.isEmpty()) {
            return 1.0;
        }
        
        if (resumeExperience == null || resumeExperience.isEmpty()) {
            return 0.0;
        }
        
        // Simple text similarity based on common keywords
        String resumeText = String.join(" ", resumeExperience).toLowerCase();
        String requiredText = String.join(" ", requiredExperience).toLowerCase();
        
        String[] requiredWords = requiredText.split("\\s+");
        int matchingWords = 0;
        
        for (String word : requiredWords) {
            if (word.length() > 3 && resumeText.contains(word)) {
                matchingWords++;
            }
        }
        
        return requiredWords.length > 0 ? (double) matchingWords / requiredWords.length : 0.0;
    }

    private double calculateEducationSimilarity(List<String> resumeEducation, List<String> requiredEducation) {
        if (requiredEducation == null || requiredEducation.isEmpty()) {
            return 1.0;
        }
        
        if (resumeEducation == null || resumeEducation.isEmpty()) {
            return 0.0;
        }
        
        // Similar to experience similarity
        String resumeText = String.join(" ", resumeEducation).toLowerCase();
        String requiredText = String.join(" ", requiredEducation).toLowerCase();
        
        String[] requiredWords = requiredText.split("\\s+");
        int matchingWords = 0;
        
        for (String word : requiredWords) {
            if (word.length() > 3 && resumeText.contains(word)) {
                matchingWords++;
            }
        }
        
        return requiredWords.length > 0 ? (double) matchingWords / requiredWords.length : 0.0;
    }

    private List<String> findMissingSkills(List<String> resumeSkills, List<String> requiredSkills) {
        if (requiredSkills == null || requiredSkills.isEmpty()) {
            return new ArrayList<>();
        }
        
        if (resumeSkills == null || resumeSkills.isEmpty()) {
            return new ArrayList<>(requiredSkills);
        }
        
        Set<String> resumeSkillsLower = resumeSkills.stream()
                .map(String::toLowerCase)
                .collect(Collectors.toSet());
        
        return requiredSkills.stream()
                .filter(skill -> !resumeSkillsLower.contains(skill.toLowerCase()))
                .collect(Collectors.toList());
    }

    private List<String> generateImprovementSuggestions(List<String> missingSkills, double skillsScore, double overallScore) {
        List<String> suggestions = new ArrayList<>();
        
        if (overallScore < 0.5) {
            suggestions.add("Your resume needs significant improvements to match this job requirement");
        }
        
        if (skillsScore < 0.5) {
            suggestions.add("Consider adding more relevant technical skills to your resume");
        }
        
        if (skillsScore < 0.7) {
            suggestions.add("Highlight your most relevant skills at the top of your resume");
        }
        
        for (String skill : missingSkills) {
            suggestions.add("Consider adding " + skill + " to your skills section or gaining experience with it");
        }
        
        if (suggestions.isEmpty()) {
            suggestions.add("Your resume looks well-aligned with the job requirements!");
        }
        
        return suggestions;
    }

    private List<String> generateLearningResources(List<String> missingSkills) {
        List<String> resources = new ArrayList<>();
        
        for (String skill : missingSkills) {
            resources.add("LinkedIn Learning: " + skill + " courses");
            resources.add("Coursera: " + skill + " specialization");
            resources.add("Udemy: " + skill + " tutorials");
            resources.add("YouTube: " + skill + " tutorial videos");
        }
        
        if (resources.isEmpty()) {
            resources.add("No specific learning resources needed. Your skills match well!");
        }
        
        return resources;
    }

    private Map<String, Double> calculateIndividualSkillScores(List<String> resumeSkills, List<String> requiredSkills) {
        Map<String, Double> skillScores = new HashMap<>();
        
        if (requiredSkills == null || requiredSkills.isEmpty()) {
            return skillScores;
        }
        
        if (resumeSkills == null || resumeSkills.isEmpty()) {
            for (String skill : requiredSkills) {
                skillScores.put(skill, 0.0);
            }
            return skillScores;
        }
        
        Set<String> resumeSkillsLower = resumeSkills.stream()
                .map(String::toLowerCase)
                .collect(Collectors.toSet());
        
        for (String skill : requiredSkills) {
            if (resumeSkillsLower.contains(skill.toLowerCase())) {
                skillScores.put(skill, 1.0);
            } else {
                skillScores.put(skill, 0.0);
            }
        }
        
        return skillScores;
    }

    public List<MatchResult> getMatchResultsForUser(Long userId) {
        return matchResultRepository.findByResumeUserId(userId);
    }

    public Optional<MatchResult> getMatchResult(Long resumeId, Long jobDescriptionId) {
        return matchResultRepository.findByResumeIdAndJobDescriptionId(resumeId, jobDescriptionId);
    }
}