package com.apiestudar.api_prodify.interfaces.dto.brasilapi;

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