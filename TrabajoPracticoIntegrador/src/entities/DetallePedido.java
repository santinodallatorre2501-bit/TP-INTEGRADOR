/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entities;

import java.time.LocalDateTime;

/**
 *
 * @author valen
 */
public class DetallePedido extends Base{
    
    private int cantidad;
    private Double subtotal;
    private Producto producto;

    public DetallePedido(int cantidad, Double subtotal, Producto producto) {
        super();
        this.cantidad = cantidad;
        this.subtotal = subtotal;
        this.producto = producto;
    }

    public DetallePedido(int cantidad, Double subtotal, Long id, boolean eliminado, LocalDateTime createdAt, Producto producto) {
        super(id, eliminado, createdAt);
        this.cantidad = cantidad;
        this.subtotal = subtotal;
        this.producto = producto;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public Double getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(Double subtotal) {
        this.subtotal = subtotal;
    }

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    @Override
    public String toString() {
        return super.toString() + " - " + cantidad + " x " + producto.getNombre() + " | Subtotal: $" + subtotal;
    }
}
