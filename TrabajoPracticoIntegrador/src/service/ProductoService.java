/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package service;

import exception.EntidadNoEncontradaException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 *
 * @author valen
 */
public class ProductoService {

    private final List<Producto> productos = new ArrayList<>();
    private final CategoriaService categoriaService;

    public ProductoService(CategoriaService categoriaService) {
        this.categoriaService = categoriaService;
    }

    public List<Producto> listar() {
        return productos.stream()
                .filter(p -> !p.isEliminado())
                .toList();
    }

    public List<Producto> listarPorCategoria(Long idCategoria) {
        return productos.stream()
                .filter(p -> !p.isEliminado()
                        && p.getCategoria() != null
                        && p.getCategoria().getId().equals(idCategoria))
                .toList();
    }

    public Producto crear(String nombre, String descripcion, double precio, int stock,
                          String imagen, boolean disponible, Long idCategoria) {

        if (nombre == null || nombre.isBlank()) {
            throw new ValidacionException("El nombre no puede estar vacío.");
        }
        if (precio < 0) {
            throw new ValidacionException("El precio no puede ser negativo.");
        }
        if (stock < 0) {
            throw new ValidacionException("El stock no puede ser negativo.");
        }

        Categoria categoria = categoriaService.listar().stream()
                .filter(c -> c.getId().equals(idCategoria))
                .findFirst()
                .orElseThrow(() -> new EntidadNoEncontradaException("La categoría no existe o está eliminada."));

        Producto nuevo = new Producto(nombre, descripcion, precio, stock, imagen, disponible, categoria);
        productos.add(nuevo);

        categoriaService.asociarProducto(categoria, nuevo);

        return nuevo;
    }

    private Producto buscarPorId(Long id) {
        Optional<Producto> opt = productos.stream()
                .filter(p -> p.getId().equals(id))
                .findFirst();

        if (opt.isEmpty() || opt.get().isEliminado()) {
            throw new EntidadNoEncontradaException("El producto no existe o está eliminado.");
        }

        return opt.get();
    }

    public Producto editar(Long id, String nombre, String descripcion, Double precio,
                           Integer stock, String imagen, Boolean disponible, Long idCategoria) {

        Producto prod = buscarPorId(id);

        if (nombre != null && !nombre.isBlank()) {
            prod.setNombre(nombre);
        }

        if (descripcion != null) {
            prod.setDescripcion(descripcion);
        }

        if (precio != null) {
            if (precio < 0) throw new ValidacionException("El precio no puede ser negativo.");
            prod.setPrecio(precio);
        }

        if (stock != null) {
            if (stock < 0) throw new ValidacionException("El stock no puede ser negativo.");
            prod.setStock(stock);
        }

        if (imagen != null) {
            prod.setImagen(imagen);
        }

        if (disponible != null) {
            prod.setDisponible(disponible);
        }

        if (idCategoria != null) {
            Categoria nuevaCat = categoriaService.listar().stream()
                    .filter(c -> c.getId().equals(idCategoria))
                    .findFirst()
                    .orElseThrow(() -> new EntidadNoEncontradaException("La categoría no existe o está eliminada."));

            prod.setCategoria(nuevaCat);
        }

        return prod;
    }

    public void eliminar(Long id) {
        Producto prod = buscarPorId(id);
        prod.setEliminado(true);
    }

    public List<Producto> getAllInternal() {
        return productos;
    }
    
}
