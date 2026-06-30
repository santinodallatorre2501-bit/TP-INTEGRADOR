/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package service;

import dao.UsuarioDAO;
import entities.Usuario;
import enums.Rol;
import exception.EntityNotFoundException;
import java.util.List;

/**
 *
 * @author valen
 */
public class UsuarioService {
    
    // Instanciamos el DAO para poder comunicarnos con la base de datos
    private UsuarioDAO usuarioDAO = new UsuarioDAO();

    /**
     * Crea un nuevo usuario validando que los datos esenciales estén presentes.
     */
    public void crearUsuario(String nombre, String apellido, String mail, String celular, String contrasena, Rol rol) {
        
        // Validación de datos 
        if (mail == null || mail.trim().isEmpty()) {
            System.out.println("El mail no puede estar vacío.");
            return; // Cortamos la ejecución para no enviar datos incompletos a la BD
        }

        // Creamos el objeto y lo mandamos al DAO
        Usuario nuevoUsuario = new Usuario(nombre, apellido, mail, celular, contrasena, rol);
        usuarioDAO.crear(nuevoUsuario);
    }

    /**
     * Lista todos los usuariosno eliminados
     */
    public void listarUsuarios() {
        List<Usuario> lista = usuarioDAO.listarTodos();
        
        if (lista.isEmpty()) {
            System.out.println("No hay usuarios registrados en el sistema.");
        } else {
            System.out.println("LISTA DE USUARIOS: ");
            for (Usuario u : lista) {
                System.out.println(u.toString());
            }
        }
    }

    
     //Actualiza un usuario existente. Si no existe, lanza una excepción
     
    public void editarUsuario(Long id, String nombre, String apellido, String mail, String celular, String contrasena, Rol rol) {
        // 1. Verificamos si el usuario realmente existe en la BD
        Usuario usuarioExistente = usuarioDAO.obtenerPorId(id);
        
        if (usuarioExistente == null) {
          //excepcion personalizada
            throw new EntityNotFoundException("No se encontró ningún usuario con el ID " + id);
        }
        
        // Si existe, actualizamos sus datos con la nueva información
        usuarioExistente.setUsuario(nombre); 
        usuarioExistente.setApellido(apellido);
        usuarioExistente.setMail(mail);
        usuarioExistente.setCelular(celular);
        usuarioExistente.setContrasena(contrasena);
        usuarioExistente.setRol(rol);
        
        // Enviamos el objeto actualizado al DAO para que ejecute el UPDATE
        usuarioDAO.actualizar(usuarioExistente);
    }

  
    public void eliminarUsuario(Long id) {
        // Verificamos si el usuario existe antes de intentar eliminarlo
        Usuario usuarioExistente = usuarioDAO.obtenerPorId(id);
        
        if (usuarioExistente == null) {
            throw new EntityNotFoundException("No se encontró ningún usuario con el ID " + id);
        }
        
        usuarioDAO.eliminar(id);
    }
}