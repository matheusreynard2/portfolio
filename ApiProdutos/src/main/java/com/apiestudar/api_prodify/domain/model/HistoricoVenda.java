package com.apiestudar.api_prodify.domain.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.CascadeType;
import jakarta.persistence.SequenceGenerator;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity(name = "historico_vendas")
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@SequenceGenerator(name = "historico_vendas_seq", sequenceName = "historico_vendas_sequence", allocationSize = 1)
@Schema(name = "HistoricoVenda Entity")
@Builder
public class HistoricoVenda {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "historico_vendas_seq")
    @Column(nullable = false)
    @Schema(description = "ID do histórico")
    private Long id;

    @ManyToOne(optional = false, cascade = CascadeType.ALL)
    @JoinColumn(name = "vendacaixa_id", nullable = false)
    @Schema(description = "Venda do caixa referenciada")
    private VendaCaixa vendaCaixa;
}


