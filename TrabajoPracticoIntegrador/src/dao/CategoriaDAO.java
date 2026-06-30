/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import java.util.Optional;

/**
 *
 * @author valen
 */
public interface CategoriaDAO extends IBaseDAO{

    Optional<Categoria> findByNombre(String nombre) throws Exception;
}
