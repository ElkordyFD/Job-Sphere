package com.jobsphere.core;

import java.util.UUID;

public class JobBuilder {
    private String title;
    private String description;
    private String companyUsername;
    private String requirements;

    public JobBuilder setTitle(String title) {
        this.title = title;
        return this;
    }

    public JobBuilder setDescription(String description) {
        this.description = description;
        return this;
    }

    public JobBuilder setCompanyUsername(String companyUsername) {
        this.companyUsername = companyUsername;
        return this;
    }

    public JobBuilder setRequirements(String requirements) {
        this.requirements = requirements;
        return this;
    }

    public Job build() {
        return new Job(UUID.randomUUID().toString(), title, description, companyUsername, requirements);
    }
}
