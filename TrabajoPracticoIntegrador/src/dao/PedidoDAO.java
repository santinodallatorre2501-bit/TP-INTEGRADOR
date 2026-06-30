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

    private static final String INSERT_PEDIDO = "INSERT INTO pedidos (eliminado, created_at, fecha, estado, total, forma_pago, usuario_id) VALUES (?, ?, ?, ?, ?, ?, ?)";
    private static final String INSERT_DETALLE = "INSERT INTO detalle_pedidos (eliminado, created_at, cantidad, subtotal, producto_id, pedido_id) VALUES (?, ?, ?, ?, ?, ?)";
    private static final String SELECT_PEDIDO_POR_ID = "SELECT * FROM pedidos WHERE id = ?";
    private static final String SELECT_ALL_PEDIDOS = "SELECT * FROM pedidos";
    private static final String UPDATE_PEDIDO = "UPDATE pedidos SET fecha = ?, estado = ?, total = ?, forma_pago = ?, usuario_id = ? WHERE id = ?";
    private static final String DELETE_PEDIDO = "UPDATE pedidos SET eliminado = true WHERE id = ?";
    
    @Override
    public void crear(Pedido entidad) {
        // Preparamos la variable para guardar la "llamada" a la base de datos
        Connection conn = null;

        try {
            // 1. ABRIR CONEXIÓN Y CONFIGURAR TRANSACCIÓN
            // Llamamos a tu clase para conectarnos a MySQL
            conn = ConexionDB.getConexion();

            // ¡Fundamental! Le decimos a MySQL: "Anotá en lápiz, no guardes nada hasta que yo te avise"
            conn.setAutoCommit(false);

            // 2. GUARDAR EL PEDIDO (EL "TICKET" PRINCIPAL)
            // Preparamos la consulta y le exigimos a MySQL que nos devuelva el ID que genere
            try (PreparedStatement pstmtPedido = conn.prepareStatement(INSERT_PEDIDO, PreparedStatement.RETURN_GENERATED_KEYS)) {

                // Rellenamos los 7 huecos (?) de la consulta INSERT_PEDIDO
                pstmtPedido.setBoolean(1, false); // eliminado
                pstmtPedido.setTimestamp(2, java.sql.Timestamp.valueOf(java.time.LocalDateTime.now())); // created_at (Hora del sistema)
                pstmtPedido.setDate(3, java.sql.Date.valueOf(entidad.getFecha())); // fecha comercial (Convertida para MySQL)
                pstmtPedido.setString(4, entidad.getEstado().name()); // estado (Convertimos el Enum a texto)
                pstmtPedido.setDouble(5, entidad.getTotal()); // total
                pstmtPedido.setString(6, entidad.getFormaPago().name()); // forma_pago (Convertimos el Enum a texto)
                pstmtPedido.setLong(7, entidad.getUsuario().getId()); // ID del cliente asociado

                // Disparamos la consulta a la memoria temporal de MySQL
                pstmtPedido.executeUpdate();

                // 3. RECUPERAR EL ID GENERADO
                // Le pedimos a MySQL la clave (el numerito) que acaba de inventar para este pedido
                ResultSet rs = pstmtPedido.getGeneratedKeys();
                Long idPedidoGenerado = null;

                // Si nos devolvió un resultado, lo sacamos y se lo guardamos a nuestro objeto Java
                if (rs.next()) {
                    idPedidoGenerado = rs.getLong(1);
                    entidad.setId(idPedidoGenerado);
                }

                // 4. GUARDAR LOS DETALLES (LOS RENGLONES DEL TICKET)
                // Preparamos el segundo molde usando la constante INSERT_DETALLE
                try (PreparedStatement pstmtDetalle = conn.prepareStatement(INSERT_DETALLE)) {

                    // Recorremos el "changuito" elemento por elemento
                    for (DetallePedido detalle : entidad.getDetalles()) {

                        // Llenamos los huecos (?) para CADA detalle
                        pstmtDetalle.setBoolean(1, false); // eliminado
                        pstmtDetalle.setTimestamp(2, java.sql.Timestamp.valueOf(java.time.LocalDateTime.now())); // created_at
                        pstmtDetalle.setInt(3, detalle.getCantidad()); // cantidad
                        pstmtDetalle.setDouble(4, detalle.getSubtotal()); // subtotal
                        pstmtDetalle.setLong(5, detalle.getProducto().getId()); // ID del producto comprado

                        // ¡Clave! Asociamos este detalle al pedido usando el ID que recuperamos arriba
                        pstmtDetalle.setLong(6, idPedidoGenerado);

                        // Disparamos este renglón a la memoria temporal
                        pstmtDetalle.executeUpdate();
                    }
                } // Acá se cierra y limpia el pstmtDetalle automáticamente

            } // Acá se cierra y limpia el pstmtPedido automáticamente

            // 5. CONFIRMAR LA TRANSACCIÓN (COMMIT)
            // Si el código llegó vivo hasta acá, significa que no hubo ningún error.
            // Le damos la orden final a MySQL: "¡Pasá todo a tinta, guardalo en el disco duro!"
            conn.commit();
            System.out.println("¡Éxito! Pedido y detalles guardados correctamente en la Base de Datos.");

        } catch (SQLException e) {

            // 6. MANEJO DE ERRORES (ROLLBACK)
            // Si algo explotó (ej: se cortó internet a mitad del for-each), el código salta directo acá
            System.err.println("Ocurrió un error al guardar. Cancelando operación... Detalles: " + e.getMessage());

            if (conn != null) {
                try {
                    // El famoso "Ctrl+Z". Le decimos a MySQL que borre lo que tenía en lápiz
                    conn.rollback();
                    System.out.println("Rollback exitoso: La base de datos no fue modificada.");
                } catch (SQLException ex) {
                    System.err.println("Error gravísimo: ¡Falló hasta el rollback! " + ex.getMessage());
                }
            }

        } finally {

            // 7. CERRAR LA CONEXIÓN
            // Este bloque se ejecuta SIEMPRE, haya habido éxito o error.
            // Es vital para "colgar el teléfono" y no saturar a MySQL de conexiones fantasmas.
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
        // 1. Preparamos una variable vacía. Si no encontramos el pedido, devolverá null.
        Pedido pedidoEncontrado = null;
        Connection conn = null;

        try {
            // 2. Abrimos la conexión a MySQL
            conn = ConexionDB.getConexion();

            // 3. Preparamos la consulta y rellenamos el hueco (?) con el ID que buscamos
            try (PreparedStatement pstmt = conn.prepareStatement(SELECT_PEDIDO_POR_ID)) {

                pstmt.setLong(1, id); // Metemos el ID en el hueco

                // 4. ¡OJO ACÁ! Usamos executeQuery() porque esperamos que MySQL nos DEVUELVA datos
                ResultSet rs = pstmt.executeQuery();

                // 5. rs.next() pregunta: "¿MySQL encontró alguna fila con ese ID?"
                if (rs.next()) {

                    // 6. Si la encontró, empezamos a desarmar la fila sacando los datos columna por columna
                    Long idPedido = rs.getLong("id");
                    boolean eliminado = rs.getBoolean("eliminado");
                    java.time.LocalDateTime createdAt = rs.getTimestamp("created_at").toLocalDateTime();
                    java.time.LocalDate fecha = rs.getDate("fecha").toLocalDate();

                    // Los Enum se recuperan como texto (String) y se vuelven a convertir a Enum con valueOf()
                    Estado estado = Estado.valueOf(rs.getString("estado"));
                    Double total = rs.getDouble("total");
                    FormaPago formaPago = FormaPago.valueOf(rs.getString("forma_pago"));

                    // 1. Agarramos de tu tabla pedidos el ID del cliente que compró
                    Long idDelUsuario = rs.getLong("usuario_id");

                    // 2. Instanciamos el DAO de tu compañero
                    UsuarioDAO usuarioDAO = new UsuarioDAO();

                    // 3. Le pedimos a SU código que vaya a la base de datos y nos arme el objeto
                    Usuario usuarioReal = usuarioDAO.obtenerPorId(idDelUsuario);

                    // 4. ENSAMBLAJE: Ahora sí, pasamos el "usuarioReal" en vez del null
                    pedidoEncontrado = new Pedido(fecha, estado, total, formaPago, usuarioReal, idPedido, eliminado, createdAt);

                }
            }

        } catch (SQLException e) {
            System.err.println("Error al buscar el pedido en la base de datos: " + e.getMessage());
        } finally {
            // 8. Como siempre, cerramos la conexión al terminar
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

        // 9. Devolvemos el pedido armado (o null si no existía el ID)
        return pedidoEncontrado;
    }

    @Override
    public List<Pedido> listarTodos() {
        // 1. Creamos una lista vacía donde vamos a meter todos los pedidos que encontremos
        List<Pedido> listaPedidos = new ArrayList<>();
        Connection conn = null;

        try {
            conn = ConexionDB.getConexion();

            // 2. Usamos el SELECT_ALL que no lleva parámetros porque trae todo
            try (PreparedStatement pstmt = conn.prepareStatement(SELECT_ALL_PEDIDOS)) {

                ResultSet rs = pstmt.executeQuery();

                // 3. ¡EL WHILE! Mientras haya una fila nueva en la tabla, el código sigue dando vueltas
                while (rs.next()) {

                    // 4. Desarmamos la fila (igual que hicimos en obtenerPorId)
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

                    // 5. ENSAMBLAJE
                    Pedido pedidoArmado = new Pedido(fecha, estado, total, formaPago, usuarioReal, idPedido, eliminado, createdAt);

                    // 6. ¡Lo metemos a la bolsa!
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

        // 7. Devolvemos la lista llena (o vacía si no había nada)
        return listaPedidos;
    }

    @Override
    public void actualizar(Pedido entidad) {
        Connection conn = null;

        try {
            conn = ConexionDB.getConexion();

            // 1. Preparamos el update usando la constante nueva
            try (PreparedStatement pstmt = conn.prepareStatement(UPDATE_PEDIDO)) {

                // 2. Rellenamos los 5 campos que vamos a modificar
                pstmt.setDate(1, java.sql.Date.valueOf(entidad.getFecha()));
                pstmt.setString(2, entidad.getEstado().name());
                pstmt.setDouble(3, entidad.getTotal());
                pstmt.setString(4, entidad.getFormaPago().name());
                pstmt.setLong(5, entidad.getUsuario().getId());

                // 3. ¡IMPORTANTE! El ID va al final, en el WHERE, para decirle a MySQL CUÁL fila editar
                pstmt.setLong(6, entidad.getId());

                // 4. Ejecutamos la actualización
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

            // 1. Preparamos el update de borrado lógico
            try (PreparedStatement pstmt = conn.prepareStatement(DELETE_PEDIDO)) {

                // 2. Le pasamos el ID del pedido que queremos "dar de baja"
                pstmt.setLong(1, id);

                // 3. Ejecutamos
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
