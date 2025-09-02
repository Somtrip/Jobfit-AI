package com.jobfit.service;

import com.jobfit.entity.JobDescription;
import com.jobfit.entity.User;
import com.jobfit.repository.JobDescriptionRepository;
import com.jobfit.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class JobDescriptionService {
    @Autowired
    private JobDescriptionRepository jobDescriptionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TextExtractionService textExtractionService;

    public JobDescription createJobDescription(JobDescription jobDescription) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        jobDescription.setUser(user);
        
        // Extract information from the description text
        if (jobDescription.getDescription() != null) {
            String extractedText = jobDescription.getDescription();
            jobDescription.setExtractedText(extractedText);
            jobDescription.setRequiredSkills(textExtractionService.extractSkills(extractedText));
            jobDescription.setExperienceRequirements(textExtractionService.extractExperience(extractedText));
            jobDescription.setEducationRequirements(textExtractionService.extractEducation(extractedText));
        }

        return jobDescriptionRepository.save(jobDescription);
    }

    public List<JobDescription> getUserJobDescriptions() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return jobDescriptionRepository.findByUserIdOrderByCreatedAtDesc(user.getId());
    }

    public Optional<JobDescription> getJobDescriptionById(Long id) {
        return jobDescriptionRepository.findById(id);
    }

    public JobDescription updateJobDescription(Long id, JobDescription jobDescriptionDetails) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        JobDescription jobDescription = jobDescriptionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Job Description not found"));

        if (!jobDescription.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized to update this job description");
        }

        jobDescription.setTitle(jobDescriptionDetails.getTitle());
        jobDescription.setCompany(jobDescriptionDetails.getCompany());
        jobDescription.setDescription(jobDescriptionDetails.getDescription());

        // Re-extract information if description changed
        if (jobDescriptionDetails.getDescription() != null) {
            String extractedText = jobDescriptionDetails.getDescription();
            jobDescription.setExtractedText(extractedText);
            jobDescription.setRequiredSkills(textExtractionService.extractSkills(extractedText));
            jobDescription.setExperienceRequirements(textExtractionService.extractExperience(extractedText));
            jobDescription.setEducationRequirements(textExtractionService.extractEducation(extractedText));
        }

        return jobDescriptionRepository.save(jobDescription);
    }

    public void deleteJobDescription(Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        JobDescription jobDescription = jobDescriptionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Job Description not found"));

        if (!jobDescription.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized to delete this job description");
        }

        jobDescriptionRepository.delete(jobDescription);
    }
} 