package com.apiestudar.api_prodify.infrastructure.persistence.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import com.apiestudar.api_prodify.domain.model.Compra;

public interface CompraJpaRepository extends JpaRepository<Compra, Long> {

    @Query("SELECT c FROM Compra c WHERE c.idUsuario = :idUsuario")
    List<Compra> listarComprasByIdUsuario(@Param("idUsuario") Long idUsuario);   
}


