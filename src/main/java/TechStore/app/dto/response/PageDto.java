package TechStore.app.dto.response;

import lombok.Data;

@Data
public class PageDto {
    private int currentPage;
    private int pageSize;
    private long totalRecords;
    private int totalPages;
}

