package dao;

import config.ConexionDB;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CategoriaDAOJdbc implements CategoriaDAO{
    private final ConexionDB conexion;

    public CategoriaDAOJdbc(ConexionDB conexion) {
        this.conexion = conexion;
    }

    @Override
    public Categoria save(Categoria categoria) throws SQLException {
        String sql = "INSERT INTO categorias(nombre, descripcion, eliminado, created_at) VALUES (?, ?, ?, ?)";
        try (Connection conn = conexion.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, categoria.getNombre());
            ps.setString(2, categoria.getDescripcion());
            ps.setBoolean(3, categoria.isEliminado());
            ps.setTimestamp(4, Timestamp.valueOf(categoria.getCreatedAt()));
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    categoria.setId(rs.getLong(1));
                }
            }
        }
        return categoria;
    }

    @Override
    public Optional<Categoria> findById(Long id) throws SQLException {
        String sql = "SELECT id, nombre, descripcion, eliminado, created_at FROM categorias WHERE id = ?";
        try (Connection conn = conexion.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Categoria c = new Categoria(rs.getString("nombre"), rs.getString("descripcion"));
                    c.setId(rs.getLong("id"));
                    c.setEliminado(rs.getBoolean("eliminado"));
                    return Optional.of(c);
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public List<Categoria> findAll() throws SQLException {
        String sql = "SELECT id, nombre, descripcion, eliminado, created_at FROM categorias";
        List<Categoria> lista = new ArrayList<>();
        try (Connection conn = conexion.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Categoria c = new Categoria(rs.getString("nombre"), rs.getString("descripcion"));
                c.setId(rs.getLong("id"));
                c.setEliminado(rs.getBoolean("eliminado"));
                lista.add(c);
            }
        }
        return lista;
    }

    @Override
    public Categoria update(Categoria categoria) throws SQLException {
        String sql = "UPDATE categorias SET nombre = ?, descripcion = ? WHERE id = ?";
        try (Connection conn = conexion.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, categoria.getNombre());
            ps.setString(2, categoria.getDescripcion());
            ps.setLong(3, categoria.getId());
            ps.executeUpdate();
        }
        return categoria;
    }

    @Override
    public void softDelete(Long id) throws SQLException {
        String sql = "UPDATE categorias SET eliminado = true WHERE id = ?";
        try (Connection conn = conexion.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        }
    }

    @Override
    public Optional<Categoria> findByNombre(String nombre) throws SQLException {
        String sql = "SELECT id, nombre, descripcion, eliminado, created_at FROM categorias WHERE lower(nombre) = lower(?)";
        try (Connection conn = conexion.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nombre);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Categoria c = new Categoria(rs.getString("nombre"), rs.getString("descripcion"));
                    c.setId(rs.getLong("id"));
                    c.setEliminado(rs.getBoolean("eliminado"));
                    return Optional.of(c);
                }
            }
        }
        return Optional.empty();
    }

}
