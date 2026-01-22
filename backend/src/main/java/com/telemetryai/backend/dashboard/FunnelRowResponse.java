package com.telemetryai.backend.dashboard;

public class FunnelRowResponse {
    private final String levelId;
    private final long starts;
    private final long endsSuccess;
    private final long endsFail;
    private final long endsQuit;
    private final Double avgDurationSec;

    public FunnelRowResponse(
            String levelId,
            long starts,
            long endsSuccess,
            long endsFail,
            long endsQuit,
            Double avgDurationSec
    ) {
        this.levelId = levelId;
        this.starts = starts;
        this.endsSuccess = endsSuccess;
        this.endsFail = endsFail;
        this.endsQuit = endsQuit;
        this.avgDurationSec = avgDurationSec;
    }

    public String getLevelId() {
        return levelId;
    }

    public long getStarts() {
        return starts;
    }

    public long getEndsSuccess() {
        return endsSuccess;
    }

    public long getEndsFail() {
        return endsFail;
    }

    public long getEndsQuit() {
        return endsQuit;
    }

    public Double getAvgDurationSec() {
        return avgDurationSec;
    }
}
