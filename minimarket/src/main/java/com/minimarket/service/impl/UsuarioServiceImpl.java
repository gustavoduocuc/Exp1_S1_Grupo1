package com.minimarket.service.impl;

import com.minimarket.dto.UsuarioRequestDto;
import com.minimarket.dto.UsuarioResponseDto;
import com.minimarket.entity.Rol;
import com.minimarket.entity.Usuario;
import com.minimarket.repository.RolRepository;
import com.minimarket.repository.UsuarioRepository;
import com.minimarket.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UsuarioServiceImpl implements UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private RolRepository rolRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public List<UsuarioResponseDto> findAll() {
        return usuarioRepository.findAll().stream()
                .map(this::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<UsuarioResponseDto> findById(Long id) {
        return usuarioRepository.findById(id).map(this::toResponseDto);
    }

    @Override
    public Optional<Usuario> findByUsername(String username) {
        return usuarioRepository.findByUsername(username);
    }

    @Override
    public UsuarioResponseDto create(UsuarioRequestDto dto) {
        Usuario usuario = new Usuario();
        usuario.setUsername(dto.getUsername());
        usuario.setPassword(encodePassword(dto.getPassword()));
        usuario.setRoles(resolveRoles(dto.getRoles()));
        return toResponseDto(usuarioRepository.save(usuario));
    }

    @Override
    public Optional<UsuarioResponseDto> update(Long id, UsuarioRequestDto dto) {
        return usuarioRepository.findById(id).map(existing -> {
            existing.setUsername(dto.getUsername());
            if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
                existing.setPassword(encodePassword(dto.getPassword()));
            }
            if (dto.getRoles() != null && !dto.getRoles().isEmpty()) {
                existing.setRoles(resolveRoles(dto.getRoles()));
            }
            return toResponseDto(usuarioRepository.save(existing));
        });
    }

    @Override
    public Usuario save(Usuario usuario) {
        if (usuario.getId() == null) {
            usuario.setPassword(encodePassword(usuario.getPassword()));
        } else {
            usuarioRepository.findById(usuario.getId()).ifPresent(existing -> {
                if (usuario.getPassword() == null || usuario.getPassword().isBlank()) {
                    usuario.setPassword(existing.getPassword());
                } else if (!isEncoded(usuario.getPassword())) {
                    usuario.setPassword(encodePassword(usuario.getPassword()));
                }
            });
        }
        return usuarioRepository.save(usuario);
    }

    @Override
    public void deleteById(Long id) {
        usuarioRepository.deleteById(id);
    }

    private UsuarioResponseDto toResponseDto(Usuario usuario) {
        Set<String> roleNames = usuario.getRoles().stream()
                .map(Rol::getNombre)
                .collect(Collectors.toSet());
        return new UsuarioResponseDto(usuario.getId(), usuario.getUsername(), roleNames);
    }

    private Set<Rol> resolveRoles(Set<String> roleNames) {
        Set<Rol> roles = new HashSet<>();
        for (String name : roleNames) {
            Rol rol = rolRepository.findByNombre(name)
                    .orElseThrow(() -> new IllegalArgumentException("Rol no encontrado: " + name));
            roles.add(rol);
        }
        return roles;
    }

    private String encodePassword(String rawPassword) {
        if (rawPassword == null || rawPassword.isBlank()) {
            throw new IllegalArgumentException("La contraseña es obligatoria");
        }
        if (isEncoded(rawPassword)) {
            return rawPassword;
        }
        return passwordEncoder.encode(rawPassword);
    }

    private boolean isEncoded(String password) {
        return password.startsWith("$2a$") || password.startsWith("$2b$");
    }
}
