package com.apiestudar.api_prodify.infrastructure.persistence.jpa.specification;

import org.springframework.data.jpa.domain.Specification;
import javax.persistence.criteria.*;

import com.apiestudar.api_prodify.domain.model.Produto;

public final class ProdutoSpecification {

    private ProdutoSpecification() { }

    public static Specification<Produto> userId(long idUsuario) {
        return (root, query, cb) -> cb.equal(root.get("idUsuario"), idUsuario);
    }

    public static Specification<Produto> idEquals(Long id) {
        return (root, query, cb) ->
                id == null ? null : cb.equal(root.get("id"), id);
    }

    public static Specification<Produto> nomeLike(String nome) {
        if (nome == null) return (r,q,c) -> null;
        String trimmed = nome.trim();
        if (trimmed.isEmpty()) return (r,q,c) -> null;
        return (root, query, cb) ->
                cb.like(cb.lower(root.get("nome")), "%" + trimmed.toLowerCase() + "%");
    }
    

    public static Specification<Produto> nomeFornecedorLike(String nomeFornecedor) {
        return (root, query, cb) -> {
            if (nomeFornecedor == null) return null;
            Join<Object, Object> fornecedor = root.join("fornecedor");
            return cb.like(cb.lower(fornecedor.get("nome")), "%" + nomeFornecedor.toLowerCase() + "%");
        };
    }

    public static Specification<Produto> valorInicialEquals(Long valorInicial) {
        return (root, query, cb) ->
            valorInicial == null ? null : cb.equal(root.get("valorInicial"), valorInicial);
    }
}
