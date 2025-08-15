package com.apiestudar.api_prodify.domain.model;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.CollectionTable;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@SequenceGenerator(name = "vendacaixa_seq", sequenceName = "vendacaixa_sequence", allocationSize = 1)
@Schema(name = "VendaCaixa Entity")
@Builder
public class VendaCaixa {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "vendacaixa_seq")
    @Column(nullable = false)
    @Schema(description = "ID da venda do caixa")
    private Long id;

    @NotNull(message = "O ID Usuário é obrigatório")
    @Schema(description = "ID do usuário")
    private Long idUsuario;

    @Schema(description = "Itens do caixa")
    @ElementCollection
    @CollectionTable(
        name = "vendacaixa_itens",
        joinColumns = @JoinColumn(name = "id_vendacaixa")
    )
    @lombok.Builder.Default
    private Set<CaixaItem> itens = new HashSet<>();

    @Schema(description = "Total de quantidades")
    private Long totalQuantidade;

    @Schema(description = "Total de valores")
    private BigDecimal totalValor;
}


