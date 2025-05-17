package com.apiestudar.api_prodify.application.usecase.usuario;

import java.util.Arrays;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.apiestudar.api_prodify.application.TokenService;
import com.apiestudar.api_prodify.domain.model.ContadorIP;
import com.apiestudar.api_prodify.domain.model.Usuario;
import com.apiestudar.api_prodify.domain.repository.ContadorIPRepository;
import com.apiestudar.api_prodify.domain.repository.UsuarioRepository;
import com.apiestudar.api_prodify.shared.utils.strategypattern.HeaderIpExtractor;
import com.apiestudar.api_prodify.shared.utils.strategypattern.IpExtractorManager;

@Service
public class UsuarioHelper {

    private final UsuarioRepository usuarioRepository;
    private final ContadorIPRepository contadorIPRepository;

    private static final int NR_MAX_REPETICOES = 0;
    

    @Autowired
    public UsuarioHelper(TokenService tokenService, UsuarioRepository usuarioRepository, ContadorIPRepository contadorIPRepository) {
        this.usuarioRepository = usuarioRepository;
        this.contadorIPRepository = contadorIPRepository;
    }
    
    public String getSenhaByLogin(String loginUsuario) {
        return usuarioRepository.buscarSenhaPorLogin(loginUsuario);
    }

    @Transactional(rollbackFor = Exception.class)
    public Usuario findByLogin(String login) {
        return usuarioRepository.buscarPorLogin(login);
    }

    public int findIPRepetido(String novoAcesso) {
        return contadorIPRepository.contarIpRepetido(novoAcesso);
    }

    public Long getTotalAcessos() {
        return contadorIPRepository.contarTotalAcessos();
    }
    
    public int contarLoginRepetido(String login) {
        return usuarioRepository.contarLoginRepetido(login);
    }

    public boolean acessoIP(HttpServletRequest req) {
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
            contadorIPRepository.adicionarIP(novoAcesso);
        }
        	return true;
    }
}
