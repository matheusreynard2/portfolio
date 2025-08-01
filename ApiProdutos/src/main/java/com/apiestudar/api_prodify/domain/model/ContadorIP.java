package com.apiestudar.api_prodify.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@SequenceGenerator(name = "contadorip_seq", sequenceName = "contadorip_sequence", allocationSize = 1)
public class ContadorIP {
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "contadorip_seq")
	@Column(name="id", nullable = false)
	@NotNull(message = "ID do contador de IP é obrigatório")
	private Long id;
	
	@Column(nullable = false, length = 50)
	@NotNull(message = "Número do IP é obrigatório")
	private String numeroIp;
	
}