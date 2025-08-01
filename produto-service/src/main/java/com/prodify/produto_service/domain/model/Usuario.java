package com.prodify.produto_service.domain.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.proxy.HibernateProxy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
@Table(name = "usuario")
@SequenceGenerator(name = "usuario_seq", sequenceName = "usuario_sequence", allocationSize = 1)
@Schema(name = "Usuario Entity")
@Builder
public class Usuario implements UserDetails {

	private static final long serialVersionUID = -5674623373800610176L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "usuario_seq")
	@Column(name="id", nullable = false)
	@NotNull(message = "ID do produto é obrigatório")
	@Schema(description = "ID do usuário")
	private Long idUsuario;

	@Column(nullable = false, length = 100)
	@NotNull(message = "Login do usuário é obrigatório")
	@Schema(description = "Login do usuário")
	private String login;
	
	@Column(nullable = false, length = 100)
	@NotNull(message = "Senha do usuário é obrigatório")
	@Schema(description = "Senha do usuário")
	private String senha;
	
	@Column(nullable = false, length = 100)
	@NotNull(message = "Email do usuário é obrigatório")
	@Schema(description = "Email do usuário")
	private String email;
	
	@Column(nullable = true)
	@Schema(description = "Token do usuário")
	private String token;
	
    @Lob
    @Column(nullable = true)
    @Schema(description = "Imagem do usuário")
    private byte[] imagem;

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	// A PARTIR DAQUI SÃO MÉTODOS DO SPRING SECURITY

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return new ArrayList<>(List.of(new SimpleGrantedAuthority("ROLE_USER")));
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

	@Override
	public final boolean equals(Object o) {
		if (this == o) return true;
		if (o == null) return false;
		Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
		Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
		if (thisEffectiveClass != oEffectiveClass) return false;
		Usuario usuario = (Usuario) o;
		return getIdUsuario() != null && Objects.equals(getIdUsuario(), usuario.getIdUsuario());
	}

	@Override
	public final int hashCode() {
		return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
	}
}