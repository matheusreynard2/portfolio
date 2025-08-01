package com.prodify.produto_service.adapter.in.web.dto.brasilapi_dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CnaeSecundarioDTO {
    private Long id;
    private String codigo;
    private String descricao;
}