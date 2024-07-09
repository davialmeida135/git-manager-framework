package com.projectmanager.config;

public enum GitServiceType {
    GITHUB("gitService"),
    GITLAB("GitlabService");

    private final String serviceName;

    GitServiceType(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getServiceName() {
        return serviceName;
    }
}
