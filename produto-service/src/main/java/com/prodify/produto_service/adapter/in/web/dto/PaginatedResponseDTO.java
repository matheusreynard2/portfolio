package com.prodify.produto_service.adapter.in.web.dto;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Schema(name = "PaginatedResponseDTO")
@Builder
public class PaginatedResponseDTO<T> {
    private List<T> content;
    private long totalElements;
    private int totalPages;
    private int size;
    private int number;

}
