package com.apiestudar.service.impl;

import java.io.IOException;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.apiestudar.entity.ContadorIP;
import com.apiestudar.entity.Usuario;
import com.apiestudar.entity.UsuarioCurso;
import com.apiestudar.exceptions.ParametroInformadoNullException;
import com.apiestudar.exceptions.RegistroNaoEncontradoException;
import com.apiestudar.pattern.HeaderIpExtractor;
import com.apiestudar.pattern.IpExtractorManager;
import com.apiestudar.repository.ContadorIPRepository;
import com.apiestudar.repository.UsuarioCursoRepository;
import com.apiestudar.repository.UsuarioRepository;
import com.apiestudar.service.TokenService;
import com.apiestudar.service.UsuarioService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class UsuarioServiceImpl implements UsuarioService {

	@Autowired
	private TokenService tokenService;

	@Autowired
	private UsuarioRepository usuarioRepository;

	@Autowired
	private ContadorIPRepository contadorIPRepository;

	@Autowired
	private UsuarioCursoRepository usuarioCursoRepository;

	private final static int NR_MAX_REPETICOES = 0;
	private final static int MAX_NUMBER_REGISTERED_LOGIN = 1;

	@Override
	public ContadorIP addNovoAcessoIp(ContadorIP novoAcesso) {
		return contadorIPRepository.save(novoAcesso);
	}
	
	private void verificarNull(Object parametro) {
		Optional.ofNullable(parametro)
        .orElseThrow(() -> new ParametroInformadoNullException());
	}

	@Override
	public Object adicionarUsuario(String usuarioJSON, MultipartFile imagemFile) throws IOException {

		verificarNull(usuarioJSON);

		if (findLoginRepetido(usuarioJSON) >= MAX_NUMBER_REGISTERED_LOGIN) {
			String errorMessage = "Login já cadastrado no banco de dados.";
			return errorMessage;
		} else {
			Usuario user = new ObjectMapper().readValue(usuarioJSON, Usuario.class);
			String imagemStringBase64 = Base64.getEncoder().encodeToString(imagemFile.getBytes());
			user.setImagem(imagemStringBase64);
			String senhaCriptografada = new BCryptPasswordEncoder().encode(user.getSenha());
			user.setSenha(senhaCriptografada);
			usuarioRepository.save(user);
			return user;
		}
	}

	@Override
	public UsuarioCurso adicionarUsuarioCurso(UsuarioCurso userCurso) {
		verificarNull(userCurso);
		return usuarioCursoRepository.save(userCurso);
	}

	@Override
	public Usuario adicionarUsuarioReact(Usuario usuario) {
		verificarNull(usuario);
		return usuarioRepository.save(usuario);
	}

	@Override
	public List<Usuario> listarUsuarios() {
		return usuarioRepository.findAll();
	}

	@Override
	public List<Usuario> listarUsuariosReact() {
		return usuarioRepository.findAll();
	}

	@Override
	public boolean deletarUsuario(long id) {
		if (usuarioRepository.findById(id).isEmpty()) {
			throw new RegistroNaoEncontradoException();
		} else {
			usuarioRepository.deleteById(id);
			return true;
		}
	}

	public String getSenhaByLogin(String loginUsuario) {
		return usuarioRepository.getSenhaByLogin(loginUsuario);
	}

	public int findLoginRepetido(String usuarioJSON) throws JsonMappingException, JsonProcessingException {
		// Converter o JSON de volta para um objeto Produto
		Usuario user = new ObjectMapper().readValue(usuarioJSON, Usuario.class);
		return usuarioRepository.findLoginRepetido(user.getLogin());
	}

	public Usuario findByLogin(String login) {
		return usuarioRepository.findByLogin(login);
	}

	public int findIPRepetido(String novoAcesso) {
		return contadorIPRepository.findIPRepetido(novoAcesso);
	}

	public long getTotalAcessos() {
		return contadorIPRepository.getTotalAcessos();
	}

	public Map<String, Object> realizarLogin(Usuario usuario) {
		
		verificarNull(usuario);
		Map<String, Object> response = new HashMap<>();
		BCryptPasswordEncoder senhaCriptografada = new BCryptPasswordEncoder();
		String senhaArmazenada = getSenhaByLogin(usuario.getLogin());
		
		if (senhaCriptografada.matches(usuario.getSenha(), senhaArmazenada)) {
			Usuario usuarioLogado = findByLogin(usuario.getLogin());
			String token = tokenService.gerarToken(usuario);
			usuarioLogado.setToken(token);
			response.put("usuario", usuarioLogado);
		} else {
			response.put("message", "Credenciais inválidas");
		}
		return response;
	}

	public long acessar(HttpServletRequest req) {
		IpExtractorManager ipExtractor = new IpExtractorManager(Arrays.asList(new HeaderIpExtractor("X-Forwarded-For"),
				new HeaderIpExtractor("Proxy-Client-IP"), new HeaderIpExtractor("WL-Proxy-Client-IP"),
				new HeaderIpExtractor("HTTP_CLIENT_IP"), new HeaderIpExtractor("HTTP_X_FORWARDED_FOR")));

		String ip = ipExtractor.extractIp(req);

		if (findIPRepetido(ip) == NR_MAX_REPETICOES) {
			ContadorIP novoAcesso = new ContadorIP();
			novoAcesso.setNumeroIp(ip);
			addNovoAcessoIp(novoAcesso);
		}

		return getTotalAcessos();
	}
}
