package com.jobfit.controller;

import com.jobfit.dto.ResumeDto;
import com.jobfit.entity.Resume;
import com.jobfit.service.ResumeService;
import org.apache.tika.exception.TikaException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/resumes")
@CrossOrigin(origins = "*", maxAge = 3600)
public class ResumeController {
    @Autowired
    private ResumeService resumeService;

    @PostMapping("/upload")
    public ResponseEntity<ResumeDto> uploadResume(@RequestParam("file") MultipartFile file) {
        try {
            ResumeDto resumeDto = resumeService.uploadResume(file);
            return ResponseEntity.ok(resumeDto);
        } catch (IOException | TikaException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping
    public ResponseEntity<List<Resume>> getUserResumes() {
        List<Resume> resumes = resumeService.getUserResumes();
        return ResponseEntity.ok(resumes);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Resume> getResumeById(@PathVariable Long id) {
        return resumeService.getResumeById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteResume(@PathVariable Long id) {
        try {
            resumeService.deleteResume(id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
} 