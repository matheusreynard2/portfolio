package com.apiestudar.service.usuario;

import java.io.IOException;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.apiestudar.model.ContadorIP;
import com.apiestudar.model.Usuario;
import com.apiestudar.model.UsuarioCurso;
import com.apiestudar.repository.ContadorIPRepository;
import com.apiestudar.repository.UsuarioCursoRepository;
import com.apiestudar.repository.UsuarioRepository;
import com.apiestudar.service.jwt.TokenService;
import com.apiestudar.strategyPattern.IpExtractorManager;
import com.apiestudar.strategyPattern.HeaderIpExtractor;
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
	
	@Override
	public ContadorIP addNovoAcessoIp(ContadorIP novoAcesso) {
		return contadorIPRepository.save(novoAcesso);
	}
	
	@Override
	public Usuario adicionarUsuario(String usuarioJSON, MultipartFile imagemFile) throws IOException {
		
		Usuario user = new ObjectMapper().readValue(usuarioJSON, Usuario.class);
		
		// Converter MultipartFile para String Base 64
        String imagemStringBase64 = Base64.getEncoder().encodeToString(imagemFile.getBytes());
        
        user.setImagem(imagemStringBase64);
    	
		String senhaCriptografada = new BCryptPasswordEncoder().encode(user.getSenha());
		
		user.setSenha(senhaCriptografada);
				
		return usuarioRepository.save(user);
	}
	
	@Override
	public UsuarioCurso adicionarUsuarioCurso(UsuarioCurso userCurso) {
		return usuarioCursoRepository.save(userCurso);
	}
	
	@Override
	public Usuario adicionarUsuarioReact(Usuario usuario) {
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
		// Procura o usuário pelo id, se encontrar e for != false ele deleta e retorna
		// "true" para o controller
		if (usuarioRepository.findById(id).isPresent()) {
			usuarioRepository.deleteById(id);
			return true;
		} else
			return false;
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
	 IpExtractorManager ipExtractor = new IpExtractorManager(Arrays.asList(
	        new HeaderIpExtractor("X-Forwarded-For"),
	        new HeaderIpExtractor("Proxy-Client-IP"),
	        new HeaderIpExtractor("WL-Proxy-Client-IP"),
	        new HeaderIpExtractor("HTTP_CLIENT_IP"),
	        new HeaderIpExtractor("HTTP_X_FORWARDED_FOR")
	    ));

	    String ip = ipExtractor.extractIp(req);

	    if (findIPRepetido(ip) == NR_MAX_REPETICOES) {
	        ContadorIP novoAcesso = new ContadorIP();
	        novoAcesso.setNumeroIp(ip);
	        addNovoAcessoIp(novoAcesso);
	    }

	    return getTotalAcessos();
	}
}
