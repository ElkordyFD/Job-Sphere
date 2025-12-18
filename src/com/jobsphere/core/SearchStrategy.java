package com.jobsphere.core;

import java.util.List;

public interface SearchStrategy {
    List<Job> search(List<Job> allJobs, String query);
}
