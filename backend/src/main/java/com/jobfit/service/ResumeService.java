package com.jobfit.service;

import com.jobfit.dto.ResumeDto;
import com.jobfit.entity.Resume;
import com.jobfit.entity.User;
import com.jobfit.repository.ResumeRepository;
import com.jobfit.repository.UserRepository;
import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
public class ResumeService {
    @Autowired
    private ResumeRepository resumeRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TextExtractionService textExtractionService;

    public ResumeDto uploadResume(MultipartFile file) throws IOException, TikaException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        String extractedText = textExtractionService.extractText(file);
        List<String> skills = textExtractionService.extractSkills(extractedText);
        List<String> experience = textExtractionService.extractExperience(extractedText);
        List<String> education = textExtractionService.extractEducation(extractedText);

        Resume resume = new Resume();
        resume.setUser(user);
        resume.setFileName(file.getOriginalFilename());
        resume.setContent(new String(file.getBytes()));
        resume.setExtractedText(extractedText);
        resume.setSkills(skills);
        resume.setExperience(experience);
        resume.setEducation(education);

        Resume savedResume = resumeRepository.save(resume);
        return ResumeDto.fromEntity(savedResume);
    }

   public List<Resume> getUserResumes() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication == null || !authentication.isAuthenticated()) {
        throw new RuntimeException("User not authenticated");
    }
    
    String username = authentication.getName();
    User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("User not found"));

    return resumeRepository.findByUserIdOrderByUploadedAtDesc(user.getId());
}

    public Optional<Resume> getResumeById(Long id) {
        return resumeRepository.findById(id);
    }

    public void deleteResume(Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Resume resume = resumeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Resume not found"));

        if (!resume.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized to delete this resume");
        }

        resumeRepository.delete(resume);
    }
} 