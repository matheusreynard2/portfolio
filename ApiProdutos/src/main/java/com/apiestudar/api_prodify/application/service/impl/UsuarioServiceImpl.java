package com.apiestudar.api_prodify.application.service.impl;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.apiestudar.api_prodify.application.service.TokenService;
import com.apiestudar.api_prodify.application.service.UsuarioService;
import com.apiestudar.api_prodify.domain.model.ContadorIP;
import com.apiestudar.api_prodify.domain.model.Usuario;
import com.apiestudar.api_prodify.domain.repository.ContadorIPRepository;
import com.apiestudar.api_prodify.domain.repository.UsuarioRepository;
import com.apiestudar.api_prodify.infrastructure.persistence.jpa.ContadorIPJpaRepository;
import com.apiestudar.api_prodify.shared.exception.ParametroInformadoNullException;
import com.apiestudar.api_prodify.shared.exception.RegistroNaoEncontradoException;
import com.apiestudar.api_prodify.shared.utils.Helper;
import com.apiestudar.api_prodify.shared.utils.strategypattern.HeaderIpExtractor;
import com.apiestudar.api_prodify.shared.utils.strategypattern.IpExtractorManager;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class UsuarioServiceImpl implements UsuarioService {

    private final TokenService tokenService;
    private final UsuarioRepository usuarioRepository;
    private final ContadorIPRepository contadorIPRepository;

    private static final int NR_MAX_REPETICOES = 0;
    private static final int MAX_NUMBER_REGISTERED_LOGIN = 1;

    @Autowired
    public UsuarioServiceImpl(TokenService tokenService, UsuarioRepository usuarioRepository, ContadorIPRepository contadorIPRepository) {
        this.tokenService = tokenService;
        this.usuarioRepository = usuarioRepository;
        this.contadorIPRepository = contadorIPRepository;
    }

    @Override
    public ContadorIP addNovoAcessoIp(ContadorIP novoAcesso) {
        return contadorIPRepository.adicionarContadorIP(novoAcesso);
    }

    private void verificarNull(Object parametro) {
        Optional.ofNullable(parametro).orElseThrow(ParametroInformadoNullException::new);
    }

    @Override
    public Object adicionarUsuario(String usuarioJSON, MultipartFile imagemFile) throws IOException {
        verificarNull(usuarioJSON);

        if (findLoginRepetido(usuarioJSON) >= MAX_NUMBER_REGISTERED_LOGIN) {
            return "Login já cadastrado no banco de dados.";
        } else {
            Usuario user = new ObjectMapper().readValue(usuarioJSON, Usuario.class);
            String imagemStringBase64 = Helper.convertToBase64(imagemFile);
            user.setImagem(imagemStringBase64);
            String senhaCriptografada = new BCryptPasswordEncoder().encode(user.getSenha());
            user.setSenha(senhaCriptografada);
            return usuarioRepository.adicionarUsuario(user);
        }
    }

    @Override
    public List<Usuario> listarUsuarios() {
        return usuarioRepository.listarUsuarios();
    }

    @Override
    public boolean deletarUsuario(long id) {
        if (usuarioRepository.buscarUsuarioPorId(id).isEmpty()) {
            throw new RegistroNaoEncontradoException();
        } else {
            usuarioRepository.deletarUsuario(id);
            return true;
        }
    }

    public String getSenhaByLogin(String loginUsuario) {
        return usuarioRepository.buscarSenhaPorLogin(loginUsuario);
    }

    public int findLoginRepetido(String usuarioJSON) throws JsonProcessingException {
        Usuario user = new ObjectMapper().readValue(usuarioJSON, Usuario.class);
        return usuarioRepository.contarLoginRepetido(user.getLogin());
    }

    public Usuario findByLogin(String login) {
        return usuarioRepository.buscarPorLogin(login);
    }

    public int findIPRepetido(String novoAcesso) {
        return contadorIPRepository.contarIpRepetido(novoAcesso);
    }

    public long getTotalAcessos() {
        return contadorIPRepository.contarTotalAcessos();
    }

    public Map<String, Object> realizarLogin(Usuario usuario) {
        verificarNull(usuario);

        BCryptPasswordEncoder senhaCriptografada = new BCryptPasswordEncoder();
        String senhaArmazenada = getSenhaByLogin(usuario.getLogin());
        Map<String, Object> response = new HashMap<>();

        if (senhaCriptografada.matches(usuario.getSenha(), senhaArmazenada)) {
            Usuario usuarioLogado = findByLogin(usuario.getLogin());
            String token = tokenService.gerarToken(usuario);
            usuarioLogado.setToken(token);
            response.put("usuario", usuarioLogado);
        } else {
            response.put("msgCredenciaisInvalidas", "Credenciais inválidas");
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
