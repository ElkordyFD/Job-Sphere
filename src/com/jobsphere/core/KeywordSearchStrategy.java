package com.jobsphere.core;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Strategy Pattern: Concrete strategy for keyword search.
 */
public class KeywordSearchStrategy implements SearchStrategy {
    @Override
    public List<Job> search(List<Job> allJobs, String query) {
        if (query == null || query.isEmpty()) {
            return allJobs;
        }
        String lowerQuery = query.toLowerCase();
        return allJobs.stream()
                .filter(job -> job.getTitle().toLowerCase().contains(lowerQuery) || 
                               job.getDescription().toLowerCase().contains(lowerQuery))
                .collect(Collectors.toList());
    }
}
