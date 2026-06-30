/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entities;

import enums.Estado;
import enums.FormaPago;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author valen
 */
public class Pedido extends Base implements Calculable {

    private LocalDate fecha;
    private Estado estado;
    private Double total;
    private FormaPago formaPago;
    private Usuario usuario;
    private List<DetallePedido> detalles;

    public Pedido(Estado estado, FormaPago formaPago, Usuario usuario) {
        super();
        this.fecha = LocalDate.now();
        this.estado = estado;
        this.total = 0.0;
        this.formaPago = formaPago;
        this.usuario = usuario;
        this.detalles = new ArrayList<>();
    }

    public Pedido(LocalDate fecha, Estado estado, Double total, FormaPago formaPago, Usuario usuario, Long id, boolean eliminado, LocalDateTime createdAt) {
        super(id, eliminado, createdAt);
        this.fecha = fecha;
        this.estado = estado;
        this.total = total;
        this.formaPago = formaPago;
        this.usuario = usuario;
        this.detalles = new ArrayList<>();
    }
    

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public Estado getEstado() {
        return estado;
    }

    public void setEstado(Estado estado) {
        this.estado = estado;
    }

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }

    public FormaPago getFormaPago() {
        return formaPago;
    }

    public void setFormaPago(FormaPago formaPago) {
        this.formaPago = formaPago;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public List<DetallePedido> getDetalles() {
        return detalles;
    }
    
    public void addDetallePedido(int cantidad, Double subtotal, Producto producto){
        DetallePedido detalle = new DetallePedido(cantidad, subtotal, producto);
        this.detalles.add(detalle);
        this.calcularTotal();
    }
    
    public DetallePedido findDetallePedidoByProducto(Producto producto) {
        int i = 0;
        while (i < detalles.size()) {
            Producto prodActual = detalles.get(i).getProducto();
            if (prodActual.getId().equals(producto.getId())) {
                return detalles.get(i);
            }
            i++;
        }
        return null;
    }
    
    public void deleteDetallePedidoByProducto(Producto producto) {
        DetallePedido detalleBuscado = findDetallePedidoByProducto(producto);
        if (detalleBuscado != null) {
            detalles.remove(detalleBuscado);
            this.calcularTotal();
        }
    }
    @Override
    public void calcularTotal() {
        this.total = 0.0;
        for (DetallePedido detalle : detalles) {
            this.total += detalle.getSubtotal();
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("==================================================\n");
        sb.append("                TICKET DE PEDIDO                  \n");
        sb.append("==================================================\n");
        sb.append(" ID Pedido: ").append(this.getId() != null ? this.getId() : "NUEVO (Sin guardar)").append("\n");
        sb.append(" Fecha:     ").append(fecha).append("\n");
        sb.append(" Cliente:   ").append(usuario != null ? usuario.getNombre() : "No asignado").append("\n");
        sb.append(" Estado:    ").append(estado).append("\n");
        sb.append(" Pago:      ").append(formaPago).append("\n");
        sb.append("--------------------------------------------------\n");
        sb.append(" DETALLES:\n");

        if (detalles == null || detalles.isEmpty()) {
            sb.append("   (El carrito está vacío)\n");
        } else {
            for (DetallePedido detalle : detalles) {
                // Acá aprovecha el toString() que ya armaste en DetallePedido
                sb.append("   ").append(detalle.toString()).append("\n");
            }
        }

        sb.append("--------------------------------------------------\n");
        sb.append(" TOTAL A PAGAR: $").append(total).append("\n");
        sb.append("==================================================");

        return sb.toString();
    }
}
