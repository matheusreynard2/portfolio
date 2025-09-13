package com.apiestudar.api_prodify.application.usecase.compras;

import com.apiestudar.api_prodify.interfaces.dto.HistoricoComprasDTO;
import com.apiestudar.api_prodify.shared.utils.Helper;

import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import org.springframework.stereotype.Service;
import com.apiestudar.api_prodify.domain.model.HistoricoCompras;
import com.apiestudar.api_prodify.domain.repository.HistoricoComprasRepository;

@Service
public class ListarComprasHistoricoUseCase {

	private HistoricoComprasRepository historicoComprasRepo;

	public ListarComprasHistoricoUseCase(HistoricoComprasRepository historicoComprasRepo) {
		this.historicoComprasRepo = historicoComprasRepo;
	}

	@Transactional(readOnly = true)
	public List<HistoricoComprasDTO> executar(Long idUsuario) {
		long t0 = System.nanoTime();
		long ns = System.nanoTime() - t0;
		
		List<HistoricoCompras> historicoCompras = historicoComprasRepo.listarHistoricoComprasByIdUsuario(idUsuario);
		
		List<HistoricoComprasDTO> historicoComprasDTO = Helper.mapClassToDTOList(historicoCompras, HistoricoComprasDTO.class);

        System.out.println("##############################");
        System.out.printf("## LISTAR HISTORICO COMPRAS %d ns ( %d ms)%n", ns, ns / 1_000_000);
        System.out.println("##############################");
		
		return historicoComprasDTO;
	}
}
