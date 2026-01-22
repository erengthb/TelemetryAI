package com.telemetryai.backend.ai;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.ZoneOffset;

@Component
public class AiReportJob {
    private final AiReportService reportService;
    private final int lookbackDays;

    public AiReportJob(
            AiReportService reportService,
            @Value("${telemetryai.jobs.ai.lookbackDays:1}") int lookbackDays
    ) {
        this.reportService = reportService;
        this.lookbackDays = lookbackDays;
    }

    @Scheduled(cron = "${telemetryai.jobs.ai.dailyCron:0 0 3 * * *}")
    public void runDaily() {
        LocalDate target = LocalDate.now(ZoneOffset.UTC).minusDays(lookbackDays);
        reportService.generateDaily(target);
    }

    @Scheduled(cron = "${telemetryai.jobs.ai.weeklyCron:0 0 4 * * MON}")
    public void runWeekly() {
        LocalDate weekEnd = LocalDate.now(ZoneOffset.UTC).minusDays(lookbackDays);
        reportService.generateWeekly(weekEnd);
    }
}
