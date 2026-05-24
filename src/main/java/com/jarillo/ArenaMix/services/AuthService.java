package com.jarillo.ArenaMix.services;

import com.jarillo.ArenaMix.dto.AuthResponseDTO;
import com.jarillo.ArenaMix.dto.LoginRequestDTO;
import com.jarillo.ArenaMix.dto.RegistroRequestDTO;
import com.jarillo.ArenaMix.excepciones.RecursoYaExisteException;
import com.jarillo.ArenaMix.models.Usuario;
import com.jarillo.ArenaMix.repositories.UsuarioRepository;
import com.jarillo.ArenaMix.seguridad.JwtUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    public AuthService(UsuarioRepository usuarioRepository,
                       PasswordEncoder passwordEncoder,
                       JwtUtil jwtUtil,
                       AuthenticationManager authenticationManager) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.authenticationManager = authenticationManager;
    }

    @Transactional
    public AuthResponseDTO registrar(RegistroRequestDTO dto) {
        if (usuarioRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new RecursoYaExisteException(
                    "Ya existe un usuario registrado con el email: " + dto.getEmail());
        }

        String rol = (dto.getRol() != null && !dto.getRol().isBlank()) ? dto.getRol() : "JUGADOR";

        Usuario usuario = new Usuario(
                dto.getUsername(),
                dto.getEmail(),
                passwordEncoder.encode(dto.getPassword()),
                rol
        );

        usuarioRepository.save(usuario);

        String token = jwtUtil.generarToken(construirUserDetails(usuario));
        return new AuthResponseDTO(token, usuario.getUsername(), usuario.getRol(), usuario.getId());
    }

    public AuthResponseDTO login(LoginRequestDTO dto) {
        // Lanza BadCredentialsException automáticamente si las credenciales son incorrectas
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(dto.getEmail(), dto.getPassword()));

        Usuario usuario = usuarioRepository.findByEmail(dto.getEmail()).orElseThrow();

        String token = jwtUtil.generarToken(construirUserDetails(usuario));
        return new AuthResponseDTO(token, usuario.getUsername(), usuario.getRol(), usuario.getId());
    }

    private UserDetails construirUserDetails(Usuario usuario) {
        return new User(
                usuario.getEmail(),
                usuario.getPasswordHash(),
                List.of(new SimpleGrantedAuthority("ROLE_" + usuario.getRol()))
        );
    }
}
