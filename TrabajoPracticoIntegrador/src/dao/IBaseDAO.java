/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import java.util.List;

/**
 *
 * @author valen
 */
// La <T> significa que es Genérica. 
// Cuando tus compañeros la usen, cambiarán la T por Categoria, Producto, etc.
public interface IBaseDAO<T> {

    // Método para insertar un nuevo registro en la base de datos
    public void crear(T entidad);

    // Método para buscar un registro específico por su ID
    public T obtenerPorId(Long id);

    // Método para traer todos los registros (que no estén eliminados)
    public List<T> listarTodos();

    // Método para guardar los cambios de un registro existente
    public void actualizar(T entidad);

    // Método para hacer la baja lógica (cambiar eliminado = true)
    public void eliminar(Long id);

}
