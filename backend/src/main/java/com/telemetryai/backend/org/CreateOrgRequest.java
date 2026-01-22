package com.telemetryai.backend.org;

import jakarta.validation.constraints.NotBlank;

public class CreateOrgRequest {
    @NotBlank
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
