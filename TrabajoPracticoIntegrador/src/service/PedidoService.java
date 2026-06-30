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
    
    // 1. Instanciamos los DAOs que vamos a necesitar.
    // El Service orquesta, así que puede pedirle cosas a varios DAOs a la vez.
    private PedidoDAO pedidoDAO = new PedidoDAO();
    private UsuarioDAO usuarioDAO = new UsuarioDAO();

    // ====================================================================
    // MÉTODO 1: CREAR PEDIDO (Cumple con HU-PED-02) [cite: 414]
    // ====================================================================
    public void registrarPedido(Pedido pedido) throws Exception {

        // A. Validación 1: ¿Tiene un usuario asignado? [cite: 221]
        if (pedido.getUsuario() == null || pedido.getUsuario().getId() == null) {
            throw new Exception("Error: El pedido no tiene un usuario asignado.");
        }

        // B. Validación 2: ¿El usuario realmente existe en MySQL y no está dado de baja? [cite: 418]
        Usuario usuarioReal = usuarioDAO.obtenerPorId(pedido.getUsuario().getId());
        if (usuarioReal == null || usuarioReal.isEliminado()) {
            throw new Exception("Error: El usuario seleccionado no existe o está inactivo.");
        }

        // C. Validación 3: ¿El pedido tiene detalles (productos)?
        if (pedido.getDetalles() == null || pedido.getDetalles().isEmpty()) {
            throw new Exception("Error: No se puede crear un pedido vacío. Debe tener al menos un producto.");
        }

        // D. Validación 4: Que la cantidad de cada detalle sea mayor a 0 [cite: 223]
        for (DetallePedido detalle : pedido.getDetalles()) {
            if (detalle.getCantidad() <= 0) {
                throw new Exception("Error: La cantidad de cada producto debe ser mayor a cero.");
            }
        }

        // E. Lógica de negocio clave: 
        // Obligatorio calcular el total usando la interfaz Calculable antes de guardar [cite: 421]
        // Esto asegura que nadie pueda mandar un pedido con un total trucho desde el menú.
        pedido.calcularTotal();

        // F. Si pasó todos los "patovicas" (validaciones), le damos la orden al DAO para que guarde
        pedidoDAO.crear(pedido);
    }

    // ====================================================================
    // MÉTODO 2: LISTAR PEDIDOS (Cumple con HU-PED-01) [cite: 408]
    // ====================================================================
    public List<Pedido> listarPedidos() {
        // Acá no hay mucha validación, simplemente le pedimos al DAO que nos traiga todo [cite: 409]
        // El Menú de consola después se va a encargar de dibujar esto lindo con un bucle for.
        return pedidoDAO.listarTodos();
    }

    // ====================================================================
    // MÉTODO 3: ACTUALIZAR ESTADO Y FORMA DE PAGO (Cumple con HU-PED-03) [cite: 430]
    // ====================================================================
    public void actualizarEstadoYPago(Long idPedido, Estado nuevoEstado, FormaPago nuevaFormaPago) throws Exception {

        // A. Primero buscamos si el pedido existe realmente en la base de datos
        Pedido pedidoExistente = pedidoDAO.obtenerPorId(idPedido);

        // B. Si el DAO nos devolvió null o el pedido está eliminado, cortamos todo
        if (pedidoExistente == null || pedidoExistente.isEliminado()) {
            throw new Exception("Error: El pedido con ID " + idPedido + " no existe o fue eliminado.");
        }

        // C. Actualizamos en memoria SOLO los datos que pide la historia de usuario [cite: 431, 435]
        pedidoExistente.setEstado(nuevoEstado);
        pedidoExistente.setFormaPago(nuevaFormaPago);

        // D. Mandamos el pedido modificado al DAO para que aplique el UPDATE
        pedidoDAO.actualizar(pedidoExistente);
    }

    // ====================================================================
    // MÉTODO 4: ELIMINAR PEDIDO - BAJA LÓGICA (Cumple con HU-PED-04) [cite: 439]
    // ====================================================================
    public void eliminarPedido(Long idPedido) throws Exception {

        // A. Verificamos que exista antes de intentar borrarlo
        Pedido pedidoExistente = pedidoDAO.obtenerPorId(idPedido);

        if (pedidoExistente == null || pedidoExistente.isEliminado()) {
            throw new Exception("Error: El pedido no existe o ya se encontraba eliminado.");
        }

        // B. Llamamos al DAO para que haga el UPDATE a eliminado = true [cite: 443]
        pedidoDAO.eliminar(idPedido);
    }
}

