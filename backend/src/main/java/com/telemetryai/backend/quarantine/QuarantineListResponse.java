package com.telemetryai.backend.quarantine;

import java.util.List;

public class QuarantineListResponse {
    private final List<QuarantineItemResponse> items;

    public QuarantineListResponse(List<QuarantineItemResponse> items) {
        this.items = items;
    }

    public List<QuarantineItemResponse> getItems() {
        return items;
    }
}
