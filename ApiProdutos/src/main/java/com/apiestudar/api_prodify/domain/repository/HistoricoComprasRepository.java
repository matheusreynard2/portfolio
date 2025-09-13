package com.apiestudar.api_prodify.domain.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.transaction.annotation.Transactional;
import com.apiestudar.api_prodify.domain.model.HistoricoCompras;


@Transactional
public interface HistoricoComprasRepository {

    HistoricoCompras salvarHistoricoCompras(HistoricoCompras historicoCompras);

    HistoricoCompras adicionarHistoricoCompras(HistoricoCompras historicoCompras);

    Optional<HistoricoCompras> buscarHistoricoComprasPorId(Long id);

    Boolean deletarHistoricoComprasPorId(Long id);

    List<HistoricoCompras> findAll();

    List<HistoricoCompras> listarHistoricoComprasByIdUsuario(Long idUsuario);

    void deleteCascadeByHistoricoComprasId(Long historicoComprasId);

    void deleteMultiComprasCascadeByHistoricoId(List<Long> ids);

}
