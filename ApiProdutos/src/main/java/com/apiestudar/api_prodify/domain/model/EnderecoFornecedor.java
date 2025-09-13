// EnderecoFornecedor.java
package com.apiestudar.api_prodify.domain.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Entity
@SequenceGenerator(name = "end_fornecedor_seq", sequenceName = "end_fornecedor_sequence", allocationSize = 1)
@JsonIgnoreProperties(ignoreUnknown = true)
@Schema(name = "EnderecoFornecedor Entity")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString
public class EnderecoFornecedor {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "end_fornecedor_seq")
    @Column(nullable = false)
    @Schema(description = "ID do endereço")
    @EqualsAndHashCode.Include
    private Long id; // use Long (wrapper) e NÃO marque com @NotNull aqui

    @Column(length = 10)
    private String nrResidencia;

    @Column(nullable = false, length = 20)
    @NotNull(message = "CEP do endereço é obrigatório")
    private String cep;

    @Column(length = 100)
    private String logradouro;

    @Column(length = 100)
    private String complemento;

    @Column(length = 100)
    private String unidade;

    @Column(length = 100)
    private String bairro;

    @Column(nullable = false, length = 100)
    private String localidade;

    @Column(nullable = false, length = 50)
    private String uf;

    @Column(length = 50)
    private String estado;

    @Column(length = 100)
    private String regiao;

    @Column(length = 100)
    private String ibge;

    @Column(length = 100)
    private String gia;

    @Column(length = 5)
    private String ddd;

    @Column(length = 100)
    private String siafi;

    @Column
    private String erro;

    @OneToOne(mappedBy = "enderecoFornecedor", fetch = FetchType.EAGER)
    @JsonIgnore
    @ToString.Exclude      // evita recursão no toString
    private Fornecedor fornecedor;
}
