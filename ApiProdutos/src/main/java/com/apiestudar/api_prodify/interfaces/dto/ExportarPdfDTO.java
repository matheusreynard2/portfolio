package com.apiestudar.api_prodify.interfaces.dto;

import java.util.List;
import java.util.Map;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor; 
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(name = "ExportarPdf")
public class ExportarPdfDTO {

	@NotNull(message = "Titulo é obrigatório.")
	@Schema(description = "Titulo do relatório.")
	private String titulo;

	@NotNull(message = "Colunas é obrigatório.")
	@Schema(description = "Colunas do relatório.")
	private List<String> colunas;

	@NotNull(message = "Linhas é obrigatório.")
	@Schema(description = "Linhas do relatório.")
	private List<Map<String, Object>> linhas;

	@NotNull(message = "Paisagem é obrigatório.")
	@Schema(description = "Paisagem do relatório.")
	private boolean paisagem;
}


