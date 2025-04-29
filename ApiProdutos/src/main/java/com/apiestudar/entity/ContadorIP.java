package com.apiestudar.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;

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
	private long id;
	
	private String numeroIp;
	
}
