package com.apiestudar.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;

@Entity
@SequenceGenerator(name = "contadorip_seq", sequenceName = "contadorip_sequence", allocationSize = 1)
public class ContadorIP {
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "contadorip_seq")
	private long id;
	
	private String numeroIp;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getNumeroIp() {
		return numeroIp;
	}

	public void setNumeroIp(String numeroIp) {
		this.numeroIp = numeroIp;
	}

}
