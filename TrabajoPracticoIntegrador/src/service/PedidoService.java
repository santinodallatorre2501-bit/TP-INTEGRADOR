/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package service;

import dao.PedidoDAO;
import dao.UsuarioDAO;
import entities.DetallePedido;
import entities.Pedido;
import entities.Usuario;
import enums.Estado;
import enums.FormaPago;
import java.util.List;

/**
 *
 * @author valen
 */
public class PedidoService {
    
    // Instanciamos los DAOs que vamos a necesitar.
    private PedidoDAO pedidoDAO = new PedidoDAO();
    private UsuarioDAO usuarioDAO = new UsuarioDAO();

    // MÉTODO 1: CREAR PEDIDO 
    public void registrarPedido(Pedido pedido) throws Exception {

        // Verificamos que tenga un usuario asignado
        if (pedido.getUsuario() == null || pedido.getUsuario().getId() == null) {
            throw new Exception("Error: El pedido no tiene un usuario asignado.");
        }

        // Verificamos que el usuario esté en la base de datos
        Usuario usuarioReal = usuarioDAO.obtenerPorId(pedido.getUsuario().getId());
        if (usuarioReal == null || usuarioReal.isEliminado()) {
            throw new Exception("Error: El usuario seleccionado no existe o está inactivo.");
        }

        // Verificamos que el pedido no esté vacío
        if (pedido.getDetalles() == null || pedido.getDetalles().isEmpty()) {
            throw new Exception("Error: No se puede crear un pedido vacío. Debe tener al menos un producto.");
        }

        // Verificamos que la cantidad de cada producto sea válida
        for (DetallePedido detalle : pedido.getDetalles()) {
            if (detalle.getCantidad() <= 0) {
                throw new Exception("Error: La cantidad de cada producto debe ser mayor a cero.");
            }
        }

        // Calculamos el total del pedido
        pedido.calcularTotal();

        // Ahora si, si pasó todas las verificaciones, le pedimos al DAO que cree el pedido
        pedidoDAO.crear(pedido);
    }

    // MÉTODO 2: LISTAR PEDIDOS 
    public List<Pedido> listarPedidos() {
        // Acá solo le pedimos al DAO que traiga los pedidos
        return pedidoDAO.listarTodos();
    }

    // MÉTODO 3: ACTUALIZAR ESTADO Y FORMA DE PAGO
    public void actualizarEstadoYPago(Long idPedido, Estado nuevoEstado, FormaPago nuevaFormaPago) throws Exception {

        // Buscamos si el pedido existe realmente en la base de datos
        Pedido pedidoExistente = pedidoDAO.obtenerPorId(idPedido);

        // Si el DAO nos devolvió null o el pedido está eliminado, cortamos todo
        if (pedidoExistente == null || pedidoExistente.isEliminado()) {
            throw new Exception("Error: El pedido con ID " + idPedido + " no existe o fue eliminado.");
        }

        // Actualizamos en memoria solo los datos necesarios
        pedidoExistente.setEstado(nuevoEstado);
        pedidoExistente.setFormaPago(nuevaFormaPago);

        // Mandamos el pedido modificado al DAO para que aplique el update
        pedidoDAO.actualizar(pedidoExistente);
    }

    // MÉTODO 4: ELIMINAR PEDIDO - BAJA LÓGICA 
    public void eliminarPedido(Long idPedido) throws Exception {

        // Verificamos que exista antes de intentar borrarlo
        Pedido pedidoExistente = pedidoDAO.obtenerPorId(idPedido);

        if (pedidoExistente == null || pedidoExistente.isEliminado()) {
            throw new Exception("Error: El pedido no existe o ya se encontraba eliminado.");
        }

        // Llamamos al DAO para que haga el update a eliminado = true 
        pedidoDAO.eliminar(idPedido);
    }
}

