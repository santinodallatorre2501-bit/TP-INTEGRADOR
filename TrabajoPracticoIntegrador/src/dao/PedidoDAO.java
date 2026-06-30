/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import config.ConexionDB;
import entities.DetallePedido;
import entities.Pedido;
import entities.Usuario;
import enums.Estado;
import enums.FormaPago;
import java.util.List;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 *
 * @author valen
 */
public class PedidoDAO implements IBaseDAO<Pedido> {

    //Sentencias para enviar consultas a la base de datos
    private static final String INSERT_PEDIDO = "INSERT INTO pedidos (eliminado, created_at, fecha, estado, total, forma_pago, usuario_id) VALUES (?, ?, ?, ?, ?, ?, ?)";
    private static final String INSERT_DETALLE = "INSERT INTO detalle_pedidos (eliminado, created_at, cantidad, subtotal, producto_id, pedido_id) VALUES (?, ?, ?, ?, ?, ?)";
    private static final String SELECT_PEDIDO_POR_ID = "SELECT * FROM pedidos WHERE id = ?";
    private static final String SELECT_ALL_PEDIDOS = "SELECT * FROM pedidos";
    private static final String UPDATE_PEDIDO = "UPDATE pedidos SET fecha = ?, estado = ?, total = ?, forma_pago = ?, usuario_id = ? WHERE id = ?";
    private static final String DELETE_PEDIDO = "UPDATE pedidos SET eliminado = true WHERE id = ?";
    
    @Override
    public void crear(Pedido entidad) {
        // Variable para llamar a la base de datos
        Connection conn = null;

        try {
            // Llamamos a la clase ConexionDB para justamente hacer la conexión con la base de datos
            conn = ConexionDB.getConexion();

            // Con esto, le decimos a la base de datos que no se guarde automáticamente, asi, si hay
            // un fallo en la mitad del proceso, no se guarden datos a la mitad, sino que se elimine todo.
            conn.setAutoCommit(false);


            // Acá armamos la consulta a MySQL con la variable hecha al inicio, y pasamos los datos para cada valor para crear el pedido
            try (PreparedStatement pstmtPedido = conn.prepareStatement(INSERT_PEDIDO, PreparedStatement.RETURN_GENERATED_KEYS)) {

                pstmtPedido.setBoolean(1, false); // eliminado
                pstmtPedido.setTimestamp(2, java.sql.Timestamp.valueOf(java.time.LocalDateTime.now())); // created_at (Hora del sistema)
                pstmtPedido.setDate(3, java.sql.Date.valueOf(entidad.getFecha())); // fecha comercial (Convertida para MySQL)
                pstmtPedido.setString(4, entidad.getEstado().name()); // estado (Convertimos el Enum a texto)
                pstmtPedido.setDouble(5, entidad.getTotal()); // total
                pstmtPedido.setString(6, entidad.getFormaPago().name()); // forma_pago (Convertimos el Enum a texto)
                pstmtPedido.setLong(7, entidad.getUsuario().getId()); // ID del cliente asociado

                // Enviamos la consulta
                pstmtPedido.executeUpdate();

                // Le pedimos a MySQL que nos de el id generado para el pedido que creamos
                ResultSet rs = pstmtPedido.getGeneratedKeys();
                Long idPedidoGenerado = null;

                // Si nos devolvió un resultado, lo sacamos y se lo guardamos a nuestro objeto Java
                if (rs.next()) {
                    idPedidoGenerado = rs.getLong(1);
                    entidad.setId(idPedidoGenerado);
                }

                // Ahora armamos la consulta para asignar valores a los detalles del pedido creado
                try (PreparedStatement pstmtDetalle = conn.prepareStatement(INSERT_DETALLE)) {

                    for (DetallePedido detalle : entidad.getDetalles()) {

                        pstmtDetalle.setBoolean(1, false); // eliminado
                        pstmtDetalle.setTimestamp(2, java.sql.Timestamp.valueOf(java.time.LocalDateTime.now())); // created_at
                        pstmtDetalle.setInt(3, detalle.getCantidad()); // cantidad
                        pstmtDetalle.setDouble(4, detalle.getSubtotal()); // subtotal
                        pstmtDetalle.setLong(5, detalle.getProducto().getId()); // ID del producto comprado

                        // Usando el id que recibimos del pedido creado, le asignamos este detalle
                        pstmtDetalle.setLong(6, idPedidoGenerado);

                        // Enviamos la consulta
                        pstmtDetalle.executeUpdate();
                    }
                } // Acá se cierra y limpia el pstmtDetalle automáticamente

            } // Acá se cierra y limpia el pstmtPedido automáticamente

            // Si no hubo errores en el camino, le confirmamos a MySQL que ahora si guarde estos cambios
            conn.commit();
            System.out.println("¡Éxito! Pedido y detalles guardados correctamente en la Base de Datos.");

        } catch (SQLException e) {

            // Si hubo algún error en la conexión, lo atajamos acá
            System.err.println("Ocurrió un error al guardar. Cancelando operación... Detalles: " + e.getMessage());

            if (conn != null) {
                try {
                    // Acá damos la orden para que eso que se estaba escribiendo, se borre y no se guarde o modifique en la base de datos
                    conn.rollback();
                    System.out.println("Rollback exitoso: La base de datos no fue modificada.");
                } catch (SQLException ex) {
                    System.err.println("Error gravísimo: ¡Falló hasta el rollback! " + ex.getMessage());
                }
            }

        } finally {

            // Cerramos la conexión con la base de datos, haya sido exitosa o errónea
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    System.err.println("Error al cerrar la conexión: " + e.getMessage());
                }
            }
        }
    }

    @Override
    public Pedido obtenerPorId(Long id) {
        // Preparamos una variable vacía. Si no encontramos el pedido, devolverá null.
        Pedido pedidoEncontrado = null;
        Connection conn = null;

        try {
            // Abrimos la conexión a MySQL
            conn = ConexionDB.getConexion();

            // Armamos la consulta y mandamos el ID buscado al valor que corresponde
            try (PreparedStatement pstmt = conn.prepareStatement(SELECT_PEDIDO_POR_ID)) {

                pstmt.setLong(1, id); 

                // Usamos executeQuery porque esperamos que nos devuelvan datos
                ResultSet rs = pstmt.executeQuery();

                // Vemos si encontramos alguna fila con este id
                if (rs.next()) {

                    // Si la encontró, empezamos a desarmar la fila sacando los datos columna por columna
                    Long idPedido = rs.getLong("id");
                    boolean eliminado = rs.getBoolean("eliminado");
                    java.time.LocalDateTime createdAt = rs.getTimestamp("created_at").toLocalDateTime();
                    java.time.LocalDate fecha = rs.getDate("fecha").toLocalDate();

                    // Los Enum se recuperan como String y se vuelven a convertir a Enum con valueOf()
                    Estado estado = Estado.valueOf(rs.getString("estado"));
                    Double total = rs.getDouble("total");
                    FormaPago formaPago = FormaPago.valueOf(rs.getString("forma_pago"));

                    // Sacamos de la tabla el id del usuario
                    Long idDelUsuario = rs.getLong("usuario_id");

                    // Instanciamos el DAO de usuario para crear un objeto usuario, con el usuario que corresponde
                    UsuarioDAO usuarioDAO = new UsuarioDAO();

                    // El DAO obitene por el id el usuario completo, y pasa sus datos a esta variable nueva
                    Usuario usuarioReal = usuarioDAO.obtenerPorId(idDelUsuario);

                    // Ahora enviamos todos los datos para instanciar el pedido buscado
                    pedidoEncontrado = new Pedido(fecha, estado, total, formaPago, usuarioReal, idPedido, eliminado, createdAt);

                }
            }

        } catch (SQLException e) {
            System.err.println("Error al buscar el pedido en la base de datos: " + e.getMessage());
        } finally {
            // Cerramos conexión
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

        // Devolvemos el pedido armado o null si no existía el ID
        return pedidoEncontrado;
    }

    @Override
    public List<Pedido> listarTodos() {
        // Creamos una lista vacía donde vamos a meter todos los pedidos que encontremos
        List<Pedido> listaPedidos = new ArrayList<>();
        Connection conn = null;

        try {
            conn = ConexionDB.getConexion();

            // Mandamos la consulta para traer todos los datos de la base de datos
            try (PreparedStatement pstmt = conn.prepareStatement(SELECT_ALL_PEDIDOS)) {

                ResultSet rs = pstmt.executeQuery();

                // Mientras haya una fila nueva, el ciclo sigue
                while (rs.next()) {

                    // Desarmamos la fila
                    Long idPedido = rs.getLong("id");
                    boolean eliminado = rs.getBoolean("eliminado");
                    java.time.LocalDateTime createdAt = rs.getTimestamp("created_at").toLocalDateTime();
                    java.time.LocalDate fecha = rs.getDate("fecha").toLocalDate();
                    Estado estado = Estado.valueOf(rs.getString("estado"));
                    Double total = rs.getDouble("total");
                    FormaPago formaPago = FormaPago.valueOf(rs.getString("forma_pago"));

                    // Pedimos el usuario al DAO correspondiente
                    Long idDelUsuario = rs.getLong("usuario_id");
                    UsuarioDAO usuarioDAO = new UsuarioDAO();
                    Usuario usuarioReal = usuarioDAO.obtenerPorId(idDelUsuario);

                    // Armamos el pedido
                    Pedido pedidoArmado = new Pedido(fecha, estado, total, formaPago, usuarioReal, idPedido, eliminado, createdAt);

                    // Añadimos en la lista de pedidos
                    listaPedidos.add(pedidoArmado);
                }
            }

        } catch (SQLException e) {
            System.err.println("Error al listar los pedidos: " + e.getMessage());
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

        // Devolvemos la lista llena o vacía si no había nada
        return listaPedidos;
    }

    @Override
    public void actualizar(Pedido entidad) {
        Connection conn = null;

        try {
            conn = ConexionDB.getConexion();

            // Preparamos la consulta para actualizar
            try (PreparedStatement pstmt = conn.prepareStatement(UPDATE_PEDIDO)) {

                pstmt.setDate(1, java.sql.Date.valueOf(entidad.getFecha()));
                pstmt.setString(2, entidad.getEstado().name());
                pstmt.setDouble(3, entidad.getTotal());
                pstmt.setString(4, entidad.getFormaPago().name());
                pstmt.setLong(5, entidad.getUsuario().getId());
                pstmt.setLong(6, entidad.getId());

                // Ejecutamos la consulta
                int filasAfectadas = pstmt.executeUpdate();

                if (filasAfectadas > 0) {
                    System.out.println("¡Éxito! Pedido ID " + entidad.getId() + " actualizado correctamente.");
                } else {
                    System.out.println("No se encontró ningún pedido con el ID " + entidad.getId());
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al actualizar el pedido: " + e.getMessage());
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void eliminar(Long id) {
        Connection conn = null;

        try {
            conn = ConexionDB.getConexion();

            // Preparamos la consulta para eliminar
            try (PreparedStatement pstmt = conn.prepareStatement(DELETE_PEDIDO)) {

                // Le pasamos el ID del pedido que queremos borrar
                pstmt.setLong(1, id);

                // Ejecutamos
                int filasAfectadas = pstmt.executeUpdate();

                if (filasAfectadas > 0) {
                    System.out.println("Pedido ID " + id + " marcado como eliminado exitosamente.");
                } else {
                    System.out.println("No se encontró el pedido con ID " + id + ".");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al intentar eliminar el pedido: " + e.getMessage());
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    
}
