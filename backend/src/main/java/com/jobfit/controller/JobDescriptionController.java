package com.jobfit.controller;

import com.jobfit.entity.JobDescription;
import com.jobfit.service.JobDescriptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/job-descriptions")
@CrossOrigin(origins = "*", maxAge = 3600)
public class JobDescriptionController {
    @Autowired
    private JobDescriptionService jobDescriptionService;

    @PostMapping
    public ResponseEntity<JobDescription> createJobDescription(@RequestBody JobDescription jobDescription) {
        JobDescription created = jobDescriptionService.createJobDescription(jobDescription);
        return ResponseEntity.ok(created);
    }

    @GetMapping
    public ResponseEntity<List<JobDescription>> getUserJobDescriptions() {
        List<JobDescription> jobDescriptions = jobDescriptionService.getUserJobDescriptions();
        return ResponseEntity.ok(jobDescriptions);
    }

    @GetMapping("/{id}")
    public ResponseEntity<JobDescription> getJobDescriptionById(@PathVariable Long id) {
        return jobDescriptionService.getJobDescriptionById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<JobDescription> updateJobDescription(@PathVariable Long id, @RequestBody JobDescription jobDescriptionDetails) {
        try {
            JobDescription updated = jobDescriptionService.updateJobDescription(id, jobDescriptionDetails);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteJobDescription(@PathVariable Long id) {
        try {
            jobDescriptionService.deleteJobDescription(id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
} 