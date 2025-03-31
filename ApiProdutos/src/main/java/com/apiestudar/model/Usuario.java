package com.apiestudar.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinTable;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Entity
@Table(name = "usuario")
@SequenceGenerator(name = "usuario_seq", sequenceName = "usuario_sequence", allocationSize = 1)
public class Usuario implements UserDetails {

	private static final long serialVersionUID = -5674623373800610176L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "usuario_seq")
	@Column(name="id")
	private long id;

	private String login;
	
	private String senha;
	
	private String token;
	
	private String nome;
	
	private String linkedin;
	
	private String whatsapp;
	
	private String endereco;
	
    // Relacionamento com a entidade de associação UsuarioCurso
    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UsuarioCurso> usuarioCursoList = new ArrayList<>();
    
    
 // Métodos auxiliares para manter a integridade do relacionamento
    public void addUsuarioCurso(UsuarioCurso usuarioCurso) {
    	usuarioCursoList.add(usuarioCurso);
        usuarioCurso.setUsuario(this);
    }

    public void removeUsuarioCurso(UsuarioCurso usuarioCurso) {
    	usuarioCursoList.remove(usuarioCurso);
        usuarioCurso.setUsuario(null);
    }

	public List<UsuarioCurso> getUsuarioCursoList() {
		return usuarioCursoList;
	}

	public void setUsuarioCursoList(List<UsuarioCurso> usuarioCursoList) {
		this.usuarioCursoList = usuarioCursoList;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getLinkedin() {
		return linkedin;
	}

	public void setLinkedin(String linkedin) {
		this.linkedin = linkedin;
	}

	public String getWhatsapp() {
		return whatsapp;
	}

	public void setWhatsapp(String whatsapp) {
		this.whatsapp = whatsapp;
	}

	public String getEndereco() {
		return endereco;
	}

	public void setEndereco(String endereco) {
		this.endereco = endereco;
	}

	@Lob
	private String imagem;

	public String getImagem() {
		return imagem;
	}

	public void setImagem(String imagem) {
		this.imagem = imagem;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getSenha() {
		return senha;
	}

	public void setSenha(String senha) {
		this.senha = senha;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return List.of(new SimpleGrantedAuthority("ROLE_USER"));
	}

	@Override
	public String getPassword() {
		return senha;
	}

	@Override
	public String getUsername() {
		return login;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}
	
}