package com.telemetryai.backend.ingestion;

import java.util.List;

public class BatchResponse {
    private final int received;
    private final int accepted;
    private final int invalid;
    private final int duplicate;
    private final List<InvalidSample> invalidSamples;

    public BatchResponse(
            int received,
            int accepted,
            int invalid,
            int duplicate,
            List<InvalidSample> invalidSamples
    ) {
        this.received = received;
        this.accepted = accepted;
        this.invalid = invalid;
        this.duplicate = duplicate;
        this.invalidSamples = invalidSamples;
    }

    public int getReceived() {
        return received;
    }

    public int getAccepted() {
        return accepted;
    }

    public int getInvalid() {
        return invalid;
    }

    public int getDuplicate() {
        return duplicate;
    }

    public List<InvalidSample> getInvalidSamples() {
        return invalidSamples;
    }
}
