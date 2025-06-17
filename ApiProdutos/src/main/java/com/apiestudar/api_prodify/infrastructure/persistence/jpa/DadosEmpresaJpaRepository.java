package com.apiestudar.api_prodify.infrastructure.persistence.jpa;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.apiestudar.api_prodify.domain.model.brasilapi.DadosEmpresa;

@Repository
public interface DadosEmpresaJpaRepository extends JpaRepository<DadosEmpresa, Long> {
    
    /**
     * Busca dados da empresa por CNPJ
     */
    Optional<DadosEmpresa> findByCnpj(String cnpj);
    
    /**
     * Verifica se existe dados da empresa com o CNPJ informado
     */
    boolean existsByCnpj(String cnpj);
    
    /**
     * Busca dados da empresa por razão social (contendo)
     */
    List<DadosEmpresa> findByRazaoSocialContainingIgnoreCase(String razaoSocial);
    
    /**
     * Busca dados da empresa por nome fantasia (contendo)
     */
    List<DadosEmpresa> findByNomeFantasiaContainingIgnoreCase(String nomeFantasia);
    
    /**
     * Busca dados da empresa por município
     */
    List<DadosEmpresa> findByMunicipio(String municipio);
    
    /**
     * Busca dados da empresa por UF
     */
    List<DadosEmpresa> findByUf(String uf);
    
    /**
     * Busca dados da empresa por situação cadastral
     */
    List<DadosEmpresa> findBySituacaoCadastral(String situacaoCadastral);
    
    /**
     * Busca dados da empresa com paginação por razão social
     */
    Page<DadosEmpresa> findByRazaoSocialContainingIgnoreCase(String razaoSocial, Pageable pageable);
    
    /**
     * Busca dados da empresa com paginação por nome fantasia
     */
    Page<DadosEmpresa> findByNomeFantasiaContainingIgnoreCase(String nomeFantasia, Pageable pageable);
    
    /**
     * Busca dados da empresa com paginação por município
     */
    Page<DadosEmpresa> findByMunicipio(String municipio, Pageable pageable);
    
    /**
     * Busca dados da empresa com paginação por UF
     */
    Page<DadosEmpresa> findByUf(String uf, Pageable pageable);
} 