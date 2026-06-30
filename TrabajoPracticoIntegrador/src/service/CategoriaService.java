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
public class CategoriaService {
    private final List<Categoria> categorias = new ArrayList<>();

    public List<Categoria> listar() {
        return categorias.stream()
                .filter(c -> !c.isEliminado())
                .toList();
    }

    public Categoria crear(String nombre, String descripcion) {
        if (nombre == null || nombre.isBlank()) {
            throw new ValidacionException("El nombre no puede estar vacío.");
        }

        boolean existe = categorias.stream()
                .anyMatch(c -> !c.isEliminado() && c.getNombre().equalsIgnoreCase(nombre));

        if (existe) {
            throw new ValidacionException("Ya existe una categoría con ese nombre.");
        }

        Categoria nueva = new Categoria(nombre, descripcion);
        categorias.add(nueva);
        return nueva;
    }

    private Categoria buscarPorId(Long id) {
        Optional<Categoria> opt = categorias.stream()
                .filter(c -> c.getId().equals(id))
                .findFirst();

        if (opt.isEmpty() || opt.get().isEliminado()) {
            throw new EntidadNoEncontradaException("La categoría no existe o está eliminada.");
        }

        return opt.get();
    }

    public Categoria editar(Long id, String nuevoNombre, String nuevaDescripcion) {
        Categoria cat = buscarPorId(id);

        if (nuevoNombre != null && !nuevoNombre.isBlank()) {
            boolean existe = categorias.stream()
                    .anyMatch(c -> !c.isEliminado()
                            && !c.getId().equals(id)
                            && c.getNombre().equalsIgnoreCase(nuevoNombre));

            if (existe) {
                throw new ValidacionException("Ya existe otra categoría con ese nombre.");
            }

            cat.setNombre(nuevoNombre);
        }

        if (nuevaDescripcion != null) {
            cat.setDescripcion(nuevaDescripcion);
        }

        return cat;
    }

    public void eliminar(Long id) {
        Categoria cat = buscarPorId(id);

        if (!cat.getProductos().isEmpty()) {
            throw new ValidacionException(
                    "No se puede eliminar la categoría porque tiene productos asociados."
            );
        }

        cat.setEliminado(true);
    }

    public void asociarProducto(Categoria categoria, Producto producto) {
        categoria.getProductos().add(producto);
    }
    
}
