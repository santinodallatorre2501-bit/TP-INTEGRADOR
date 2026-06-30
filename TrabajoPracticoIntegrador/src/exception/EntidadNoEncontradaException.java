/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package exception;

/**
 *
 * @author valen
 */

public class EntidadNoEncontradaException extends RuntimeException{
    /**
     * Constructor que recibe el mensaje de error.
     * @param mensaje El texto que explica qué fue lo que no se encontró.
     */
    public EntidadNoEncontradaException(String mensaje) {
        // La palabra reservada 'super' le pasa el mensaje a la clase padre (RuntimeException)
        // para que Java sepa cómo procesar el texto del error.
        super(mensaje);
    }
}
