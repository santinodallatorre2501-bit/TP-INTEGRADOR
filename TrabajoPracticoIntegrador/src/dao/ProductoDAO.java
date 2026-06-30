/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import java.util.List;

/**
 *
 * @author valen
 */
public interface ProductoDAO extends IBaseDAO {

    List<Producto> findByCategoriaId(Long categoriaId) throws Exception;
}
