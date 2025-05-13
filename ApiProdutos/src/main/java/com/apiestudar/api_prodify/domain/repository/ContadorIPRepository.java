package com.apiestudar.api_prodify.domain.repository;

import java.util.Optional;
import java.util.List;

import com.apiestudar.api_prodify.domain.model.ContadorIP;

public interface ContadorIPRepository {

    ContadorIP adicionarContadorIP(ContadorIP contadorIP);

    List<ContadorIP> listarTodos();

    Optional<ContadorIP> buscarPorId(Long id);

    int contarIpRepetido(String novoAcesso);

    long contarTotalAcessos();
}
