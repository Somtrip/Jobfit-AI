package com.jobfit.controller;

import com.jobfit.entity.JobDescription;
import com.jobfit.entity.MatchResult;
import com.jobfit.entity.Resume;
import com.jobfit.service.JobDescriptionService;
import com.jobfit.service.MatchingService;
import com.jobfit.service.ResumeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/matching")
@CrossOrigin(origins = "*", maxAge = 3600)
public class MatchingController {
    @Autowired
    private MatchingService matchingService;

    @Autowired
    private ResumeService resumeService;

    @Autowired
    private JobDescriptionService jobDescriptionService;

    @PostMapping("/match")
    public ResponseEntity<MatchResult> matchResumeToJob(@RequestParam Long resumeId, @RequestParam Long jobDescriptionId) {
        Resume resume = resumeService.getResumeById(resumeId)
                .orElse(null);
        JobDescription jobDescription = jobDescriptionService.getJobDescriptionById(jobDescriptionId)
                .orElse(null);

        if (resume == null || jobDescription == null) {
            return ResponseEntity.badRequest().build();
        }

        MatchResult matchResult = matchingService.matchResumeToJob(resume, jobDescription);
        return ResponseEntity.ok(matchResult);
    }

    @GetMapping("/results")
    public ResponseEntity<List<MatchResult>> getUserMatchResults() {
        // This would need to be implemented to get the current user's ID
        // For now, returning empty list
        return ResponseEntity.ok(List.of());
    }

    @GetMapping("/results/{resumeId}/{jobDescriptionId}")
    public ResponseEntity<MatchResult> getMatchResult(@PathVariable Long resumeId, @PathVariable Long jobDescriptionId) {
        return matchingService.getMatchResult(resumeId, jobDescriptionId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
} 