package com.example.ai_travel_agent_app.dto.customer;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WorkerSearchResponse {
    private List<WorkerCardDTO> workers;
    private long totalElements;
    private int totalPages;
    private int currentPage;
    private int pageSize;
    private boolean hasNext;
    private boolean hasPrevious;
    private SearchMetadata metadata;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class SearchMetadata {
        private long searchTime; // Thời gian tìm kiếm (ms)
        private String appliedFilters; // Các filter đã áp dụng
        private List<String> suggestedKeywords; // Từ khóa gợi ý
    }
}
