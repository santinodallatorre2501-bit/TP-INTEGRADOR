/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package exception;

/**
 *
 * @author valen
 */
public class EntityNotFoundException extends RuntimeException{
  
    public EntityNotFoundException(String mensaje) {
       
        super(mensaje);
    }
}
