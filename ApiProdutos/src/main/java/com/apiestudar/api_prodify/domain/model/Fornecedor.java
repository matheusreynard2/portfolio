// Fornecedor.java
package com.apiestudar.api_prodify.domain.model;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import com.apiestudar.api_prodify.domain.model.brasilapi_model.DadosEmpresa;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Entity
@SequenceGenerator(name = "fornecedor_seq", sequenceName = "fornecedor_sequence", allocationSize = 1)
@Schema(name = "Fornecedor Entity")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString
public class Fornecedor {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "fornecedor_seq")
    @Column(name = "id", nullable = false)
    @Schema(description = "ID do fornecedor")
    @EqualsAndHashCode.Include
    private Long id; // NÃO use @NotNull em id gerado

    @Column(length = 100, nullable = false)
    @NotNull(message = "Nome do fornecedor é obrigatório")
    private String nome;

    @Column(nullable = false)
    @NotNull(message = "ID do usuário é obrigatório")
    private Long idUsuario;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    @JoinColumn(name = "endereco_id", referencedColumnName = "id", nullable = false)
    @NotNull(message = "Endereço do fornecedor é obrigatório")
    @ToString.Exclude      // evita recursão no toString
    private EnderecoFornecedor enderecoFornecedor;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    @JoinColumn(name = "dados_empresa_id", referencedColumnName = "id")
    @ToString.Exclude
    private DadosEmpresa dadosEmpresa;

    @JsonIgnore
    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "fornecedor", referencedColumnName = "id")
    @ToString.Exclude
    private Set<Produto> produtos = new HashSet<>();
}
