package com.minimarket.config;

import com.minimarket.entity.Categoria;
import com.minimarket.entity.Producto;
import com.minimarket.entity.Rol;
import com.minimarket.entity.Usuario;
import com.minimarket.repository.CategoriaRepository;
import com.minimarket.repository.ProductoRepository;
import com.minimarket.repository.RolRepository;
import com.minimarket.service.UsuarioService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class DataInitializer implements CommandLineRunner {

    private final RolRepository rolRepository;
    private final CategoriaRepository categoriaRepository;
    private final ProductoRepository productoRepository;
    private final UsuarioService usuarioService;

    public DataInitializer(RolRepository rolRepository,
                           CategoriaRepository categoriaRepository,
                           ProductoRepository productoRepository,
                           UsuarioService usuarioService) {
        this.rolRepository = rolRepository;
        this.categoriaRepository = categoriaRepository;
        this.productoRepository = productoRepository;
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

        Categoria bebidas = createCategoria("Bebidas");
        Categoria lacteos = createCategoria("Lacteos");
        Categoria abarrotes = createCategoria("Abarrotes");

        createProducto("Leche entera 1L", 1200.0, 50, lacteos);
        createProducto("Agua mineral 500ml", 800.0, 100, bebidas);
        createProducto("Arroz 1kg", 1500.0, 30, abarrotes);
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

    private Categoria createCategoria(String nombre) {
        Categoria categoria = new Categoria();
        categoria.setNombre(nombre);
        return categoriaRepository.save(categoria);
    }

    private void createProducto(String nombre, Double precio, Integer stock, Categoria categoria) {
        Producto producto = new Producto();
        producto.setNombre(nombre);
        producto.setPrecio(precio);
        producto.setStock(stock);
        producto.setCategoria(categoria);
        productoRepository.save(producto);
    }
}
