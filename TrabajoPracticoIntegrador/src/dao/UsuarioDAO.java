/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import config.ConexionDB;
import entities.Usuario;
import java.util.ArrayList;
import java.util.List;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 *
 * @author valen
 */
public class UsuarioDAO implements IBaseDAO<Usuario> {

    @Override
    public void crear(Usuario usuario) {
        String sql = "INSERT INTO usuarios (nombre, apellido, mail, celular, contraseña, rol, eliminado, createdAt) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = ConexionDB.getConexion(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, usuario.getUsuario());
            stmt.setString(2, usuario.getApellido());
            stmt.setString(3, usuario.getMail());
            stmt.setString(4, usuario.getCelular());
            stmt.setString(5, usuario.getContrasena());
            stmt.setString(6, usuario.getRol().name());
            stmt.setBoolean(7, usuario.isEliminado());
            stmt.setTimestamp(8, java.sql.Timestamp.valueOf(usuario.getCreatedAt()));

            int filas = stmt.executeUpdate();
            if (filas > 0) {
                System.out.println("Usuario registrado.");
            }

        } catch (SQLException e) {
            System.err.println("Error al guardar usuario: " + e.getMessage());
        }
    }

    @Override
    public List<Usuario> listarTodos() {
        // Creamos una lista vacía para guardar los usuarios
        List<Usuario> listaUsuarios = new ArrayList<>();

        String sql = "SELECT * FROM usuarios WHERE eliminado = false";

        try (Connection conn = ConexionDB.getConexion(); PreparedStatement stmt = conn.prepareStatement(sql); java.sql.ResultSet rs = stmt.executeQuery()) {

            // El bucle while se repite mientras haya una siguiente fila 
            while (rs.next()) {
                Usuario usuario = new Usuario();

                //Extraemos y guardamos cada dato en el objeto
                usuario.setId(rs.getLong("id"));
                usuario.setUsuario(rs.getString("nombre"));
                usuario.setApellido(rs.getString("apellido"));
                usuario.setMail(rs.getString("mail"));
                usuario.setCelular(rs.getString("celular"));
                usuario.setContrasena(rs.getString("contraseña"));
                usuario.setRol(enums.Rol.valueOf(rs.getString("rol")));
                usuario.setEliminado(rs.getBoolean("eliminado"));
                usuario.setCreatedAt(rs.getTimestamp("createdAt").toLocalDateTime());

                // Agregamos el usuario completo a nuestra lista
                listaUsuarios.add(usuario);
            }

        } catch (SQLException e) {
            System.err.println("Error al listar los usuarios: " + e.getMessage());
        }

        return listaUsuarios;
    }

    @Override
    public void actualizar(Usuario usuario) {
        // Armamos la consulta UPDATE. Actualizamos todos los campos donde el ID coincida
        String sql = "UPDATE usuarios SET nombre = ?, apellido = ?, mail = ?, celular = ?, contraseña = ?, rol = ? WHERE id = ? AND eliminado = false";

        try (Connection conn = ConexionDB.getConexion(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            //Reemplazamos los '?' 
            stmt.setString(1, usuario.getUsuario());
            stmt.setString(2, usuario.getApellido());
            stmt.setString(3, usuario.getMail());
            stmt.setString(4, usuario.getCelular());
            stmt.setString(5, usuario.getContrasena());
            stmt.setString(6, usuario.getRol().name());
            stmt.setLong(7, usuario.getId());

            // Ejecutamos la actualización
            int filasAfectadas = stmt.executeUpdate();

            //  Verificamos el resultado
            if (filasAfectadas > 0) {
                System.out.println(" Usuario actualizado correctamente en la base de datos.");
            } else {
                System.out.println("No se pudo actualizar");
            }

        } catch (SQLException e) {
            System.err.println("Error al actualizar el usuario: " + e.getMessage());
        }
    }

    @Override
    public void eliminar(Long id) {
        String sql = "UPDATE usuarios SET eliminado = true WHERE id = ?";
        try (Connection conn = ConexionDB.getConexion(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            int filasAfectadas = stmt.executeUpdate();

            if (filasAfectadas > 0) {
                System.out.println("Usuario eliminado con éxito");
            } else {
                System.out.println("No se encontró un usuario con ese ID");
            }

        } catch (SQLException e) {
            System.err.println("Error al eliminar el usuario: " + e.getMessage());
        }
    }

    @Override
    public Usuario obtenerPorId(Long id) {
        Usuario usuarioEncontrado = null;
        //  se escribe el SQL con un ? para el ID
        String sql = "SELECT * FROM usuarios WHERE id = ? AND eliminado = false";

        // se abre el Connection y preparamos el PreparedStatement
        try (Connection conn = ConexionDB.getConexion(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            //  Reemplazamos el ? por el ID que nos pasaron
            stmt.setLong(1, id);

            // Ejecutamos la consulta
            try (java.sql.ResultSet rs = stmt.executeQuery()) {

                // rs.next se mueve a la primera fila. Si hay datos, entra al if
                if (rs.next()) {
                    usuarioEncontrado = new Usuario();

                    //  Extraemos los datos de la tabla 
                    usuarioEncontrado.setId(rs.getLong("id"));
                    usuarioEncontrado.setUsuario(rs.getString("nombre"));
                    usuarioEncontrado.setApellido(rs.getString("apellido"));
                    usuarioEncontrado.setMail(rs.getString("mail"));
                    usuarioEncontrado.setCelular(rs.getString("celular"));
                    usuarioEncontrado.setContrasena(rs.getString("contraseña"));
                    usuarioEncontrado.setRol(enums.Rol.valueOf(rs.getString("rol")));
                    usuarioEncontrado.setEliminado(rs.getBoolean("eliminado"));
                    usuarioEncontrado.setCreatedAt(rs.getTimestamp("createdAt").toLocalDateTime());

                }
            }

        } catch (SQLException e) {
            System.err.println("Error al buscar el usuario " + e.getMessage());
        }

        // Retornamos el objeto lleno o null 
        return usuarioEncontrado;
    }
}
