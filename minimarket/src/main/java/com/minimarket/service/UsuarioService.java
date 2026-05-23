package com.minimarket.service;

import com.minimarket.dto.UsuarioRequestDto;
import com.minimarket.dto.UsuarioResponseDto;
import com.minimarket.entity.Usuario;

import java.util.List;
import java.util.Optional;

public interface UsuarioService {
    List<UsuarioResponseDto> findAll();
    Optional<UsuarioResponseDto> findById(Long id);
    Optional<Usuario> findByUsername(String username);
    UsuarioResponseDto create(UsuarioRequestDto dto);
    Optional<UsuarioResponseDto> update(Long id, UsuarioRequestDto dto);
    Usuario save(Usuario usuario);
    void deleteById(Long id);
}
