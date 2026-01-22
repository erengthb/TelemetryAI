package com.telemetryai.backend.aggregation;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.ZoneOffset;

@Component
public class AggregationJob {
    private final AggregationService aggregationService;
    private final int lookbackDays;

    public AggregationJob(
            AggregationService aggregationService,
            @Value("${telemetryai.jobs.aggregation.lookbackDays:1}") int lookbackDays
    ) {
        this.aggregationService = aggregationService;
        this.lookbackDays = lookbackDays;
    }

    @Scheduled(cron = "${telemetryai.jobs.aggregation.cron:0 0 2 * * *}")
    public void runDaily() {
        LocalDate date = LocalDate.now(ZoneOffset.UTC).minusDays(lookbackDays);
        aggregationService.aggregateDaily(date);
    }
}
