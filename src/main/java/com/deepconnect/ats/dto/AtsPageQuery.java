package com.deepconnect.ats.dto;

import lombok.Data;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@Data
public class AtsPageQuery {

    private Integer pageNum = 1;
    private Integer pageSize = 10;
    private String orderBy;
    private Boolean isAsc = false;

    public Pageable toPageable() {
        int page = (pageNum != null && pageNum > 0) ? pageNum - 1 : 0;
        int size = (pageSize != null && pageSize > 0) ? pageSize : 10;

        if (orderBy != null && !orderBy.trim().isEmpty()) {
            Sort sort = Boolean.TRUE.equals(isAsc) ? Sort.by(orderBy).ascending() : Sort.by(orderBy).descending();
            return PageRequest.of(page, size, sort);
        }
        return PageRequest.of(page, size);
    }
}
