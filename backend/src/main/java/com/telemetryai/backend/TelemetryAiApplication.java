package com.telemetryai.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class TelemetryAiApplication {
    public static void main(String[] args) {
        SpringApplication.run(TelemetryAiApplication.class, args);
    }
}
