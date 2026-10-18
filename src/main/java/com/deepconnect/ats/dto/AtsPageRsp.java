package com.deepconnect.ats.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AtsPageRsp<T> {

    @Builder.Default
    private int currentSize = 0;

    @Builder.Default
    private int pageNum = 1;

    @Builder.Default
    private long totalCount = 0;

    @Builder.Default
    private int totalPage = 0;

    @Builder.Default
    private boolean hasPreviousPage = false;

    @Builder.Default
    private boolean hasNextPage = false;

    @Builder.Default
    private List<T> dataList = new ArrayList<>();

    public static <T> AtsPageRsp<T> of(Page<T> page) {
        return AtsPageRsp.<T>builder()
                .currentSize(page.getNumberOfElements())
                .pageNum(page.getNumber() + 1)
                .totalCount(page.getTotalElements())
                .totalPage(page.getTotalPages())
                .hasPreviousPage(page.hasPrevious())
                .hasNextPage(page.hasNext())
                .dataList(page.getContent())
                .build();
    }

    public static <T, R> AtsPageRsp<R> of(Page<T> page, List<R> transformedContent) {
        return AtsPageRsp.<R>builder()
                .currentSize(transformedContent.size())
                .pageNum(page.getNumber() + 1)
                .totalCount(page.getTotalElements())
                .totalPage(page.getTotalPages())
                .hasPreviousPage(page.hasPrevious())
                .hasNextPage(page.hasNext())
                .dataList(transformedContent)
                .build();
    }

    public static <T> AtsPageRsp<T> empty() {
        return AtsPageRsp.<T>builder().build();
    }
}
