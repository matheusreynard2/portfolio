package com.apiestudar.api_prodify.domain.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.apiestudar.api_prodify.domain.model.brasilapi_model.DadosEmpresa;

public interface DadosEmpresaRepository {

    /**
     * Salva os dados da empresa
     */
    DadosEmpresa salvarDadosEmpresa(DadosEmpresa dadosEmpresa);
    
    /**
     * Busca dados da empresa por ID
     */
    Optional<DadosEmpresa> buscarDadosEmpresaPorId(Long id);
    
    /**
     * Busca dados da empresa por CNPJ
     */
    Optional<DadosEmpresa> buscarDadosEmpresaPorCnpj(String cnpj);
    
    /**
     * Verifica se existe dados da empresa com o CNPJ informado
     */
    boolean existeDadosEmpresaPorCnpj(String cnpj);
    
    /**
     * Lista todos os dados das empresas
     */
    List<DadosEmpresa> listarDadosEmpresas();
    
    /**
     * Lista dados das empresas com paginação
     */
    Page<DadosEmpresa> listarDadosEmpresas(Pageable pageable);
    
    /**
     * Busca dados da empresa por razão social
     */
    List<DadosEmpresa> buscarDadosEmpresaPorRazaoSocial(String razaoSocial);
    
    /**
     * Busca dados da empresa por nome fantasia
     */
    List<DadosEmpresa> buscarDadosEmpresaPorNomeFantasia(String nomeFantasia);
    
    /**
     * Busca dados da empresa por município
     */
    List<DadosEmpresa> buscarDadosEmpresaPorMunicipio(String municipio);
    
    /**
     * Busca dados da empresa por UF
     */
    List<DadosEmpresa> buscarDadosEmpresaPorUf(String uf);
    
    /**
     * Busca dados da empresa por situação cadastral
     */
    List<DadosEmpresa> buscarDadosEmpresaPorSituacaoCadastral(String situacaoCadastral);
    
    /**
     * Busca dados da empresa por razão social com paginação
     */
    Page<DadosEmpresa> buscarDadosEmpresaPorRazaoSocial(String razaoSocial, Pageable pageable);
    
    /**
     * Busca dados da empresa por nome fantasia com paginação
     */
    Page<DadosEmpresa> buscarDadosEmpresaPorNomeFantasia(String nomeFantasia, Pageable pageable);
    
    /**
     * Busca dados da empresa por município com paginação
     */
    Page<DadosEmpresa> buscarDadosEmpresaPorMunicipio(String municipio, Pageable pageable);
    
    /**
     * Busca dados da empresa por UF com paginação
     */
    Page<DadosEmpresa> buscarDadosEmpresaPorUf(String uf, Pageable pageable);
    
    /**
     * Atualiza os dados da empresa
     */
    DadosEmpresa atualizarDadosEmpresa(DadosEmpresa dadosEmpresa);
    
    /**
     * Remove os dados da empresa por ID
     */
    void deletarDadosEmpresaPorId(Long id);
    
    /**
     * Remove os dados da empresa por CNPJ
     */
    void deletarDadosEmpresaPorCnpj(String cnpj);
} 