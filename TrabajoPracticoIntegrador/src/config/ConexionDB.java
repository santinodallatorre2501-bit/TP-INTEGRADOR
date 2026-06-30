/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package config;

/**
 *
 * @author valen
 */
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionDB {

    // 1. Definimos las credenciales y la ruta de nuestra base de datos.
    // El formato es: jdbc:mysql://[servidor]:[puerto]/[nombre_base_datos]
    private static final String URL = "jdbc:mysql://localhost:3306/pedidos_db";
    
    // Tu usuario de MySQL (por defecto suele ser root)
    private static final String USUARIO = "root";
    
    // Reemplaza este valor por tu contraseña real de MySQL
    private static final String PASSWORD = "root"; 

    /**
     * Método estático para obtener la conexión a la base de datos.
     * Al ser estático, podemos llamarlo sin necesidad de instanciar un objeto de esta clase.
     * * @return Connection si la conexión es exitosa, o null si falla.
     */
    public static Connection getConexion() {
        Connection conexion = null;
        
        try {
            // Intentamos establecer la conexión usando la clase DriverManager
            conexion = DriverManager.getConnection(URL, USUARIO, PASSWORD);
        } catch (SQLException e) {
            // Si ocurre algún problema (clave incorrecta, base de datos apagada, etc.), lo atrapamos aquí
            System.err.println("No se pudo establecer la conexión con la base de datos.");
            System.err.println("Detalle del error: " + e.getMessage());
        }
        
        return conexion;
    }
}