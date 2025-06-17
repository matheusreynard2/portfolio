package com.apiestudar.api_prodify.infrastructure.persistence.adapter;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.apiestudar.api_prodify.domain.model.brasilapi.DadosEmpresa;
import com.apiestudar.api_prodify.domain.repository.DadosEmpresaRepository;
import com.apiestudar.api_prodify.infrastructure.persistence.jpa.DadosEmpresaJpaRepository;

@Repository
@Transactional
public class DadosEmpresaRepositoryImpl implements DadosEmpresaRepository {
	
	private final DadosEmpresaJpaRepository dadosEmpresaJpaRepository;
	
    public DadosEmpresaRepositoryImpl(DadosEmpresaJpaRepository dadosEmpresaJpaRepository) {
        this.dadosEmpresaJpaRepository = dadosEmpresaJpaRepository;
    }
	
	@Override
	public DadosEmpresa salvarDadosEmpresa(DadosEmpresa dadosEmpresa) {
		return dadosEmpresaJpaRepository.save(dadosEmpresa);
	}
	
	@Override
	public Optional<DadosEmpresa> buscarDadosEmpresaPorId(Long id) {
		return dadosEmpresaJpaRepository.findById(id);
	}
	
	@Override
	public Optional<DadosEmpresa> buscarDadosEmpresaPorCnpj(String cnpj) {
		return dadosEmpresaJpaRepository.findByCnpj(cnpj);
	}
	
	@Override
	public boolean existeDadosEmpresaPorCnpj(String cnpj) {
		return dadosEmpresaJpaRepository.existsByCnpj(cnpj);
	}
	
	@Override
	public List<DadosEmpresa> listarDadosEmpresas() {
		return dadosEmpresaJpaRepository.findAll();
	}
	
	@Override
	public Page<DadosEmpresa> listarDadosEmpresas(Pageable pageable) {
		return dadosEmpresaJpaRepository.findAll(pageable);
	}
	
	@Override
	public List<DadosEmpresa> buscarDadosEmpresaPorRazaoSocial(String razaoSocial) {
		return dadosEmpresaJpaRepository.findByRazaoSocialContainingIgnoreCase(razaoSocial);
	}
	
	@Override
	public List<DadosEmpresa> buscarDadosEmpresaPorNomeFantasia(String nomeFantasia) {
		return dadosEmpresaJpaRepository.findByNomeFantasiaContainingIgnoreCase(nomeFantasia);
	}
	
	@Override
	public List<DadosEmpresa> buscarDadosEmpresaPorMunicipio(String municipio) {
		return dadosEmpresaJpaRepository.findByMunicipio(municipio);
	}
	
	@Override
	public List<DadosEmpresa> buscarDadosEmpresaPorUf(String uf) {
		return dadosEmpresaJpaRepository.findByUf(uf);
	}
	
	@Override
	public List<DadosEmpresa> buscarDadosEmpresaPorSituacaoCadastral(String situacaoCadastral) {
		return dadosEmpresaJpaRepository.findBySituacaoCadastral(situacaoCadastral);
	}
	
	@Override
	public Page<DadosEmpresa> buscarDadosEmpresaPorRazaoSocial(String razaoSocial, Pageable pageable) {
		return dadosEmpresaJpaRepository.findByRazaoSocialContainingIgnoreCase(razaoSocial, pageable);
	}
	
	@Override
	public Page<DadosEmpresa> buscarDadosEmpresaPorNomeFantasia(String nomeFantasia, Pageable pageable) {
		return dadosEmpresaJpaRepository.findByNomeFantasiaContainingIgnoreCase(nomeFantasia, pageable);
	}
	
	@Override
	public Page<DadosEmpresa> buscarDadosEmpresaPorMunicipio(String municipio, Pageable pageable) {
		return dadosEmpresaJpaRepository.findByMunicipio(municipio, pageable);
	}
	
	@Override
	public Page<DadosEmpresa> buscarDadosEmpresaPorUf(String uf, Pageable pageable) {
		return dadosEmpresaJpaRepository.findByUf(uf, pageable);
	}
	
	@Override
	public DadosEmpresa atualizarDadosEmpresa(DadosEmpresa dadosEmpresa) {
		return dadosEmpresaJpaRepository.save(dadosEmpresa);
	}
	
	@Override
	public void deletarDadosEmpresaPorId(Long id) {
		dadosEmpresaJpaRepository.deleteById(id);
	}
	
	@Override
	public void deletarDadosEmpresaPorCnpj(String cnpj) {
		Optional<DadosEmpresa> dadosEmpresa = dadosEmpresaJpaRepository.findByCnpj(cnpj);
		if (dadosEmpresa.isPresent()) {
			dadosEmpresaJpaRepository.delete(dadosEmpresa.get());
		}
	}
} 