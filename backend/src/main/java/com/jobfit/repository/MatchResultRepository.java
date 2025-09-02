package com.jobfit.repository;

import com.jobfit.entity.MatchResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MatchResultRepository extends JpaRepository<MatchResult, Long> {
    List<MatchResult> findByResumeUserId(Long userId);
    List<MatchResult> findByJobDescriptionUserId(Long userId);
    Optional<MatchResult> findByResumeIdAndJobDescriptionId(Long resumeId, Long jobDescriptionId);
    List<MatchResult> findByResumeIdAndJobDescriptionIdOrderByOverallScoreDesc(Long resumeId, Long jobDescriptionId);
} 