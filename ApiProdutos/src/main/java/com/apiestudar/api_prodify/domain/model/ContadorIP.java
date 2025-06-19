package com.apiestudar.api_prodify.domain.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Builder;
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
