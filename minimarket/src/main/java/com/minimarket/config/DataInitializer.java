package com.minimarket.config;

import com.minimarket.entity.Rol;
import com.minimarket.entity.Usuario;
import com.minimarket.repository.RolRepository;
import com.minimarket.service.UsuarioService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class DataInitializer implements CommandLineRunner {

    private final RolRepository rolRepository;
    private final UsuarioService usuarioService;

    public DataInitializer(RolRepository rolRepository, UsuarioService usuarioService) {
        this.rolRepository = rolRepository;
        this.usuarioService = usuarioService;
    }

    @Override
    public void run(String... args) {
        if (rolRepository.count() > 0) {
            return;
        }

        Rol cliente = createRole("CLIENTE");
        Rol empleado = createRole("EMPLEADO");
        Rol gerente = createRole("GERENTE");
        Rol admin = createRole("ADMIN");

        createUser("admin", "admin123", admin);
        createUser("gerente", "gerente123", gerente);
        createUser("empleado", "empleado123", empleado);
        createUser("cliente", "cliente123", cliente);
    }

    private Rol createRole(String nombre) {
        Rol rol = new Rol();
        rol.setNombre(nombre);
        return rolRepository.save(rol);
    }

    private void createUser(String username, String password, Rol rol) {
        Usuario usuario = new Usuario();
        usuario.setUsername(username);
        usuario.setPassword(password);
        usuario.setRoles(Set.of(rol));
        usuarioService.save(usuario);
    }
}
