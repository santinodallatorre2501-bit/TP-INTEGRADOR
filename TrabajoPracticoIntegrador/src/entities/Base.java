/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entities;

import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicLong;

/**
 *
 * @author valen
 */
public abstract class Base {
    private static final AtomicLong ID_GENERATOR = new AtomicLong(1);

    protected Long id;
    protected boolean eliminado;
    protected LocalDateTime createdAt;

    public Base() {
        this.id = ID_GENERATOR.getAndIncrement();
        this.eliminado = false;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public boolean isEliminado() {
        return eliminado;
    }

    public void setEliminado(boolean eliminado) {
        this.eliminado = eliminado;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Base base = (Base) o;
        return id != null && id.equals(base.id);
    }

    @Override
    public int hashCode() {
        return id == null ? 0 : id.hashCode();
    }
}