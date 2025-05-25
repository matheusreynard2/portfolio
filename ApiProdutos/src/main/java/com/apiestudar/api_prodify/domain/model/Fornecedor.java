package com.apiestudar.api_prodify.domain.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@SequenceGenerator(name = "fornecedor_seq", sequenceName = "fornecedor_sequence", allocationSize = 1)
@ToString
public class Fornecedor {
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "fornecedor_seq")
	private long id;
	private String nome;
	private String nrResidencia; 
	
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "endereco_id", referencedColumnName = "id")
    @JsonManagedReference
	private EnderecoFornecedor enderecoFornecedor;

    @JsonManagedReference
    @OneToMany(mappedBy = "fornecedor", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Produto> produtos = new ArrayList<>();
}
