# Food Store - Sistema de Gestión de Pedidos de Comida

Este proyecto es una aplicación de consola desarrollada en **Java 21** para la gestión de productos, categorías, usuarios y pedidos de un negocio de comidas. Se implementa Programación Orientada a Objetos (POO), persistencia de datos mediante **JDBC puro** y el patrón de diseño **DAO** para separar la lógica de negocio del acceso a datos.

## Requisitos Previos
* Java Development Kit (JDK) 21.
* Servidor MySQL instalado y corriendo.
* IDE recomendado: Apache NetBeans, IntelliJ IDEA o Eclipse.

## Configuración de la Base de Datos
Para que el sistema funcione correctamente, se debe preparar la base de datos siguiendo estos pasos:

1. Abrir su gestor de bases de datos MySQL (por ejemplo, MySQL Workbench).
2. Ejecutar el script completo provisto en el archivo `schema.sql` (ubicado en la raíz de este proyecto). Este script se encargará automáticamente de crear la base de datos `pedidos_db`, generar todas las tablas e insertar los datos iniciales.

## Configuración de la Conexión a BD
Antes de ejecutar el proyecto, es obligatorio configurar las credenciales de conexión:
1. Buscar la clase `ConexionDB.java` (ubicada en el paquete `config`).
2. Modificar las variables de la URL, el `user` (generalmente root) y el `password` para que coincidan con los de su servidor MySQL local.

## Ejecución del Sistema
1. Compilar el proyecto en su IDE.
2. Ejecutar la clase principal: `Main.java` (ubicada en el paquete `Main`).
3. El sistema desplegará un menú interactivo en la consola. La navegación se realiza ingresando los números de las opciones disponibles y presionando *Enter*. 

## Documentación
* **Documentación Técnica y Académica (PDF):** Adjuntado en el mismo archivo .zip del cual extrajo este README.
* *Nota: La demostración del flujo del sistema se realizará de manera presencial, por lo que no se incluye link de video en esta entrega.*

---
**Desarrollado por:**
* Berdejo Tomás
* Dalla Torre Santino
* Tello Valentín