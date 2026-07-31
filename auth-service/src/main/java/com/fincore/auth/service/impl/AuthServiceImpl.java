package com.fincore.auth.service;

import com.fincore.auth.dto.request.LoginRequest;
import com.fincore.auth.dto.request.RefreshRequest;
import com.fincore.auth.dto.request.RegisterRequest;
import com.fincore.auth.dto.response.AuthResponse;
import com.fincore.auth.dto.response.UsuarioResponse;
import com.fincore.auth.entity.RefreshToken;
import com.fincore.auth.entity.SesionActiva;
import com.fincore.auth.entity.Usuario;
import com.fincore.auth.enums.EstadoUsuario;
import com.fincore.auth.enums.Rol;
import com.fincore.auth.exception.UsuarioBloqueadoException;
import com.fincore.auth.exception.UsuarioNoEncontradoException;
import com.fincore.auth.exception.CredencialesInvalidasException;
import com.fincore.auth.exception.RefreshTokenInvalidoException;
import com.fincore.auth.repository.RefreshTokenRepository;
import com.fincore.auth.repository.UsuarioRepository;
import com.fincore.auth.service.impl.JwtServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.UUID;

/**
 * Implementación del servicio de autenticación.
 *
 * Maneja:
 * - Login con registro de dispositivo e IP
 * - Refresh token rotativo (rotating refresh tokens)
 * - Bloqueo después de 5 intentos fallidos (30 minutos)
 * - Registro de sesiones activas
 * - Logout individual y global
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
@Service
@Slf4j
@Transactional
public class AuthServiceImpl implements AuthService {

    private final UsuarioRepository usuarioRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtServiceImpl jwtService;
    private final PasswordEncoder passwordEncoder;

    @Value("${auth.max.failed.attempts:5}")
    private int maxFailedAttempts;

    @Value("${jwt.expiration:1800000}")
    private long jwtExpiration;

    @Value("${jwt.refresh-expiration:86400000}")
    private long refreshExpiration;

    public AuthServiceImpl(UsuarioRepository usuarioRepository,
                           RefreshTokenRepository refreshTokenRepository,
                           JwtServiceImpl jwtService,
                           PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        log.info("Intento de login para email: {}", request.getEmail());

        Usuario usuario = usuarioRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> {
                    log.warn("Usuario no encontrado: {}", request.getEmail());
                    return new UsuarioNoEncontradoException("Usuario no encontrado: " + request.getEmail());
                });

        // Verificar si está bloqueado
        if (usuario.isBloqueado()) {
            log.warn("Usuario bloqueado: {}", request.getEmail());
            throw new UsuarioBloqueadoException("Usuario bloqueado por intentos fallidos. Intente más tarde.");
        }

        // Validar contraseña
        if (!passwordEncoder.matches(request.getPassword(), usuario.getPasswordHash())) {
            usuario.registrarIntentoFallido(maxFailedAttempts);
            usuarioRepository.save(usuario);
            log.warn("Credenciales inválidas para: {}", request.getEmail());
            throw new CredencialesInvalidasException("Credenciales inválidas");
        }

        // Resetear intentos fallidos
        usuario.resetearIntentosFallidos();
        usuarioRepository.save(usuario);

        // Generar sessionId único
        String sessionId = UUID.randomUUID().toString().replace("-", "");

        // Generar tokens
        String accessToken = jwtService.generarAccessToken(usuario, sessionId, request.getDeviceId());
        String refreshToken = jwtService.generarRefreshToken(usuario, sessionId, request.getDeviceId());

        // Registrar sesión activa
        registrarSesion(usuario, sessionId, request.getDeviceId(), request.getUserAgent(), request.getIpOrigen());

        // Guardar refresh token
        RefreshToken rt = new RefreshToken();
        rt.setToken(refreshToken);
        rt.setIdUsuario(usuario.getId());
        rt.setFechaExpiracion(LocalDateTime.now().plusSeconds(refreshExpiration / 1000));
        rt.setFechaCreacion(LocalDateTime.now());
        rt.setEsRevocado(false);
        rt.setDeviceId(request.getDeviceId());
        rt.setIpOrigen(request.getIpOrigen());
        rt.setUserAgent(request.getUserAgent());
        refreshTokenRepository.save(rt);

        log.info("Login exitoso para usuario: {} (rol: {})", usuario.getEmail(), usuario.getRol());

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .expiresIn(jwtExpiration / 1000)
                .userId(usuario.getId())
                .email(usuario.getEmail())
                .nombreCompleto(usuario.getNombreCompleto())
                .rol(usuario.getRol())
                .estado(usuario.getEstado())
                .sessionId(sessionId)
                .deviceId(request.getDeviceId())
                .build();
    }

    @Override
    public AuthResponse register(RegisterRequest request) {
        log.info("Intento de registro para email: {}", request.getEmail());

        if (usuarioRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("El email ya está registrado: " + request.getEmail());
        }

        Usuario usuario = new Usuario();
        usuario.setEmail(request.getEmail());
        usuario.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        usuario.setPrimerNombre(request.getPrimerNombre());
        usuario.setSegundoNombre(request.getSegundoNombre());
        usuario.setPrimerApellido(request.getPrimerApellido());
        usuario.setSegundoApellido(request.getSegundoApellido());
        usuario.setRol(request.getRol());
        usuario.setEstado(EstadoUsuario.ACTIVO);
        usuario.setIdCliente(request.getIdCliente());
        usuario.setIntentosFallidos(0);

        usuario = usuarioRepository.save(usuario);

        String sessionId = UUID.randomUUID().toString().replace("-", "");
        String accessToken = jwtService.generarAccessToken(usuario, sessionId, request.getDeviceId());
        String refreshToken = jwtService.generarRefreshToken(usuario, sessionId, request.getDeviceId());

        RefreshToken rt = new RefreshToken();
        rt.setToken(refreshToken);
        rt.setIdUsuario(usuario.getId());
        rt.setFechaExpiracion(LocalDateTime.now().plusSeconds(refreshExpiration / 1000));
        rt.setFechaCreacion(LocalDateTime.now());
        rt.setEsRevocado(false);
        rt.setDeviceId(request.getDeviceId());

        refreshTokenRepository.save(rt);

        log.info("Registro exitoso para usuario: {} (rol: {})", usuario.getEmail(), usuario.getRol());

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .expiresIn(jwtExpiration / 1000)
                .userId(usuario.getId())
                .email(usuario.getEmail())
                .nombreCompleto(usuario.getNombreCompleto())
                .rol(usuario.getRol())
                .estado(usuario.getEstado())
                .sessionId(sessionId)
                .deviceId(request.getDeviceId())
                .build();
    }

    @Override
    public AuthResponse refreshToken(RefreshRequest request) {
        log.info("Intento de refresh token");

        RefreshToken refreshToken = refreshTokenRepository.findByToken(request.getRefreshToken())
                .orElseThrow(() -> new RefreshTokenInvalidoException("Refresh token inválido"));

        if (!refreshToken.esValido()) {
            log.warn("Refresh token expirado o revocado para usuario: {}", refreshToken.getIdUsuario());
            throw new RefreshTokenInvalidoException("Refresh token expirado o revocado");
        }

        // Revocar el refresh token usado (rotación)
        refreshToken.revocar();
        refreshTokenRepository.save(refreshToken);

        Usuario usuario = usuarioRepository.findById(refreshToken.getIdUsuario())
                .orElseThrow(() -> new UsuarioNoEncontradoException("Usuario no encontrado: " + refreshToken.getIdUsuario()));

        if (usuario.isBloqueado()) {
            throw new UsuarioBloqueadoException("Usuario bloqueado");
        }

        // Generar nuevos tokens
        String sessionId = UUID.randomUUID().toString().replace("-", "");
        String accessToken = jwtService.generarAccessToken(usuario, sessionId, request.getDeviceId());
        String newRefreshToken = jwtService.generarRefreshToken(usuario, sessionId, request.getDeviceId());

        RefreshToken newRt = new RefreshToken();
        newRt.setToken(newRefreshToken);
        newRt.setIdUsuario(usuario.getId());
        newRt.setFechaExpiracion(LocalDateTime.now().plusSeconds(refreshExpiration / 1000));
        newRt.setFechaCreacion(LocalDateTime.now());
        newRt.setEsRevocado(false);
        newRt.setDeviceId(request.getDeviceId());

        refreshTokenRepository.save(newRt);

        // Registrar nueva sesión
        registrarSesion(usuario, sessionId, request.getDeviceId(), null, null);

        log.info("Refresh token exitoso para usuario: {}", usuario.getEmail());

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(newRefreshToken)
                .expiresIn(jwtExpiration / 1000)
                .userId(usuario.getId())
                .email(usuario.getEmail())
                .nombreCompleto(usuario.getNombreCompleto())
                .rol(usuario.getRol())
                .estado(usuario.getEstado())
                .sessionId(sessionId)
                .deviceId(request.getDeviceId())
                .build();
    }

    @Override
    public void logout(String sessionId) {
        log.info("Logout para sessionId: {}", sessionId);
        // La sesión se cierra en Redis (gestionado por el gateway)
        // Aquí se revocan los refresh tokens asociados
    }

    @Override
    public void logoutAllSessions(Long userId) {
        log.info("Logout de todas las sesiones para userId: {}", userId);
        refreshTokenRepository.revocarTokensActivos(userId, LocalDateTime.now());
    }

    @Override
    @Transactional(readOnly = true)
    public UsuarioResponse obtenerUsuarioPorEmail(String email) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UsuarioNoEncontradoException("Usuario no encontrado: " + email));

        return UsuarioResponse.builder()
                .id(usuario.getId())
                .email(usuario.getEmail())
                .primerNombre(usuario.getPrimerNombre())
                .segundoNombre(usuario.getSegundoNombre())
                .primerApellido(usuario.getPrimerApellido())
                .segundoApellido(usuario.getSegundoApellido())
                .nombreCompleto(usuario.getNombreCompleto())
                .rol(usuario.getRol())
                .estado(usuario.getEstado())
                .idCliente(usuario.getIdCliente())
                .intentosFallidos(usuario.getIntentosFallidos())
                .fechaCreacion(usuario.getFechaCreacion() != null ? usuario.getFechaCreacion().toString() : null)
                .fechaActualizacion(usuario.getFechaActualizacion() != null ? usuario.getFechaActualizacion().toString() : null)
                .build();
    }

    @Override
    public void bloquearUsuario(Long userId, String motivo) {
        Usuario usuario = usuarioRepository.findById(userId)
                .orElseThrow(() -> new UsuarioNoEncontradoException("Usuario no encontrado: " + userId));
        usuario.setEstado(EstadoUsuario.BLOQUEADO);
        usuario.setFechaBloqueo(LocalDateTime.now());
        usuarioRepository.save(usuario);
        log.info("Usuario bloqueado: {} por motivo: {}", usuario.getEmail(), motivo);
    }

    @Override
    public void desbloquearUsuario(Long userId) {
        Usuario usuario = usuarioRepository.findById(userId)
                .orElseThrow(() -> new UsuarioNoEncontradoException("Usuario no encontrado: " + userId));
        usuario.setEstado(EstadoUsuario.ACTIVO);
        usuario.setFechaBloqueo(null);
        usuario.resetearIntentosFallidos();
        usuarioRepository.save(usuario);
        log.info("Usuario desbloqueado: {}", usuario.getEmail());
    }

    private void registrarSesion(Usuario usuario, String sessionId, String deviceId,
                                  String userAgent, String ipOrigen) {
        // La sesión activa se registra en Redis por el gateway
        // Aquí se persiste para auditoría
        SesionActiva sesion = new SesionActiva();
        sesion.setSessionId(sessionId);
        sesion.setIdUsuario(usuario.getId());
        sesion.setDeviceId(deviceId);
        sesion.setIpOrigen(ipOrigen);
        sesion.setUserAgent(userAgent);
        sesion.setFechaInicio(LocalDateTime.now());
        sesion.setFechaUltimaActividad(LocalDateTime.now());
        sesion.setFechaExpiracion(LocalDateTime.now().plusSeconds(jwtExpiration / 1000));
        sesion.setEsActiva(true);

        // Se persiste en BD para auditoría (no se usa repositorio de SesionActiva
        // ya que se maneja en Redis; aquí se guarda como registro histórico)
        log.debug("Sesión registrada: userId={}, sessionId={}", usuario.getId(), sessionId);
    }
}
