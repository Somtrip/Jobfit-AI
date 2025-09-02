package com.jobfit.service;

import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class TextExtractionService {
    private final Tika tika = new Tika();

    public String extractText(MultipartFile file) throws IOException, TikaException {
        return tika.parseToString(file.getInputStream());
    }

    
    @Autowired
    private HuggingFaceService huggingFaceService;
    
    public List<String> extractSkills(String text) {
        // Use Hugging Face NER to extract skills more accurately
        Map<String, List<String>> entities = huggingFaceService.extractEntities(text);
        
        List<String> skills = new ArrayList<>();
        if (entities.containsKey("SKILL")) {
            skills.addAll(entities.get("SKILL"));
        }
        
        // Fall back to regex if Hugging Face doesn't return good results
        if (skills.isEmpty()) {
            skills = extractSkillsWithRegex(text);
        }
        
        return skills;
    }
    
    public List<String> extractSkillsWithRegex(String text) {
        List<String> skills = new ArrayList<>();
        
        // Common technical skills patterns
        String[] skillPatterns = {
            "\\b(Java|Python|JavaScript|React|Angular|Vue|Node\\.js|Spring|Hibernate|MySQL|PostgreSQL|MongoDB|AWS|Azure|Docker|Kubernetes|Git|Jenkins|Maven|Gradle)\\b",
            "\\b(HTML|CSS|SQL|REST|API|JSON|XML|GraphQL|Microservices|Agile|Scrum|DevOps|CI/CD)\\b",
            "\\b(Machine Learning|AI|Data Science|Statistics|R|TensorFlow|PyTorch|Scikit-learn|Pandas|NumPy|Matplotlib)\\b",
            "\\b(Project Management|Leadership|Communication|Problem Solving|Analytical|Critical Thinking|Teamwork|Time Management)\\b"
        };

        for (String pattern : skillPatterns) {
            Pattern p = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE);
            Matcher m = p.matcher(text);
            while (m.find()) {
                String skill = m.group(1);
                if (!skills.contains(skill.toLowerCase())) {
                    skills.add(skill);
                }
            }
        }

        return skills;
    }

    public List<String> extractExperience(String text) {
        List<String> experience = new ArrayList<>();
        
        // Look for experience patterns
        String[] experiencePatterns = {
            "\\b(\\d+)\\s*(?:years?|yrs?)\\s*(?:of\\s+)?(?:experience|exp)\\b",
            "\\b(?:worked|experience)\\s+(?:as|in)\\s+([^\\n]+?)\\b",
            "\\b(?:senior|junior|lead|principal|staff)\\s+([^\\n]+?)\\b"
        };

        for (String pattern : experiencePatterns) {
            Pattern p = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE);
            Matcher m = p.matcher(text);
            while (m.find()) {
                String exp = m.group(0);
                if (!experience.contains(exp)) {
                    experience.add(exp);
                }
            }
        }

        return experience;
    }

    public List<String> extractEducation(String text) {
        List<String> education = new ArrayList<>();
        
        // Look for education patterns
        String[] educationPatterns = {
            "\\b(Bachelor|Master|PhD|BSc|MSc|MBA|Associate|Diploma|Certificate)\\b",
            "\\b(Computer Science|Engineering|Information Technology|Business|Management|Marketing|Finance)\\b",
            "\\b(University|College|Institute|School)\\b"
        };

        for (String pattern : educationPatterns) {
            Pattern p = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE);
            Matcher m = p.matcher(text);
            while (m.find()) {
                String edu = m.group(0);
                if (!education.contains(edu)) {
                    education.add(edu);
                }
            }
        }

        return education;
    }
} 