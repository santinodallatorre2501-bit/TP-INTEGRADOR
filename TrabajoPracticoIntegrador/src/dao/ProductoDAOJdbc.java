package dao;

import config.ConexionDB;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ProductoDAOJdbc implements ProductoDAO {

    private final ConexionDB conexion;
    private final CategoriaDAO categoriaDAO;

    public ProductoDAOJdbc(ConexionDB conexion, CategoriaDAO categoriaDAO) {
        this.conexion = conexion;
        this.categoriaDAO = categoriaDAO;
    }

    @Override
    public Producto save(Producto producto) throws SQLException {
        String sql = "INSERT INTO productos(nombre, descripcion, precio, stock, imagen, disponible, categoria_id, eliminado, created_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = conexion.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, producto.getNombre());
            ps.setString(2, producto.getDescripcion());
            ps.setDouble(3, producto.getPrecio());
            ps.setInt(4, producto.getStock());
            ps.setString(5, producto.getImagen());
            ps.setBoolean(6, producto.isDisponible());
            ps.setObject(7, producto.getCategoria() == null ? null : producto.getCategoria().getId());
            ps.setBoolean(8, producto.isEliminado());
            ps.setTimestamp(9, Timestamp.valueOf(producto.getCreatedAt()));
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    producto.setId(rs.getLong(1));
                }
            }
        }
        return producto;
    }

    @Override
    public Optional<Producto> findById(Long id) throws SQLException {
        String sql = "SELECT id, nombre, descripcion, precio, stock, imagen, disponible, categoria_id, eliminado, created_at FROM productos WHERE id = ?";
        try (Connection conn = conexion.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Long catId = rs.getObject("categoria_id") == null ? null : rs.getLong("categoria_id");
                    Categoria cat = null;
                    if (catId != null) {
                        cat = categoriaDAO.findById(catId).orElse(null);
                    }
                    Producto p = new Producto(rs.getString("nombre"), rs.getString("descripcion"),
                            rs.getDouble("precio"), rs.getInt("stock"), rs.getString("imagen"),
                            rs.getBoolean("disponible"), cat);
                    p.setId(rs.getLong("id"));
                    p.setEliminado(rs.getBoolean("eliminado"));
                    return Optional.of(p);
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public List<Producto> findAll() throws SQLException {
        String sql = "SELECT id, nombre, descripcion, precio, stock, imagen, disponible, categoria_id, eliminado, created_at FROM productos";
        List<Producto> lista = new ArrayList<>();
        try (Connection conn = conexion.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Long catId = rs.getObject("categoria_id") == null ? null : rs.getLong("categoria_id");
                Categoria cat = null;
                if (catId != null) {
                    cat = categoriaDAO.findById(catId).orElse(null);
                }
                Producto p = new Producto(rs.getString("nombre"), rs.getString("descripcion"),
                        rs.getDouble("precio"), rs.getInt("stock"), rs.getString("imagen"),
                        rs.getBoolean("disponible"), cat);
                p.setId(rs.getLong("id"));
                p.setEliminado(rs.getBoolean("eliminado"));
                lista.add(p);
            }
        }
        return lista;
    }

    @Override
    public Producto update(Producto producto) throws SQLException {
        String sql = "UPDATE productos SET nombre = ?, descripcion = ?, precio = ?, stock = ?, imagen = ?, disponible = ?, categoria_id = ? WHERE id = ?";
        try (Connection conn = conexion.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, producto.getNombre());
            ps.setString(2, producto.getDescripcion());
            ps.setDouble(3, producto.getPrecio());
            ps.setInt(4, producto.getStock());
            ps.setString(5, producto.getImagen());
            ps.setBoolean(6, producto.isDisponible());
            ps.setObject(7, producto.getCategoria() == null ? null : producto.getCategoria().getId());
            ps.setLong(8, producto.getId());
            ps.executeUpdate();
        }
        return producto;
    }

    @Override
    public void softDelete(Long id) throws SQLException {
        String sql = "UPDATE productos SET eliminado = true WHERE id = ?";
        try (Connection conn = conexion.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        }
    }

    @Override
    public List<Producto> findByCategoriaId(Long categoriaId) throws SQLException {
        String sql = "SELECT id, nombre, descripcion, precio, stock, imagen, disponible, categoria_id, eliminado, created_at FROM productos WHERE categoria_id = ?";
        List<Producto> lista = new ArrayList<>();
        try (Connection conn = conexion.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, categoriaId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Long catId = rs.getObject("categoria_id") == null ? null : rs.getLong("categoria_id");
                    Categoria cat = null;
                    if (catId != null) {
                        cat = categoriaDAO.findById(catId).orElse(null);
                    }
                    Producto p = new Producto(rs.getString("nombre"), rs.getString("descripcion"),
                            rs.getDouble("precio"), rs.getInt("stock"), rs.getString("imagen"),
                            rs.getBoolean("disponible"), cat);
                    p.setId(rs.getLong("id"));
                    p.setEliminado(rs.getBoolean("eliminado"));
                    lista.add(p);
                }
            }
        }
        return lista;
    }
}
