package com.jobsphere.core;

import java.util.List;

/**
 * Strategy Pattern: Interface for job search algorithms.
 */
public interface SearchStrategy {
    List<Job> search(List<Job> allJobs, String query);
}
